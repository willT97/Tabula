package will.main;



import java.util.*;


public class Board implements BoardInterface {

    private String boardName;

    private List<LocationInterface> onBoardLocations = new ArrayList<>();
    private LocationInterface start = new Location("Start");
    private LocationInterface end = new Location("End");
    private LocationInterface knockedOff = new Location("KnockedOff");


    public Board() {

        for (int i = 1; i < BoardInterface.NUMBER_OF_LOCATIONS + 1; i++) {
            String locationName = "" + i;
            LocationInterface location = new Location(locationName);
            onBoardLocations.add(location);
        }

        start.setMixed(true);
        end.setMixed(true);
        knockedOff.setMixed(true);

        for (int i = 0; i < BoardInterface.PIECES_PER_PLAYER; i++) {
            try {
                start.addPieceGetKnocked(Colour.values()[0]);
                start.addPieceGetKnocked(Colour.values()[1]);
            } catch (IllegalMoveException e) {
                e.printStackTrace();
            }
        }
    }
    //constructor for cloning
    public Board(Board board) {
        start = new Location(board.getStartLocation());
        end = new Location(board.getEndLocation());
        knockedOff = new Location(board.getKnockedLocation());

        start.setMixed(true);
        end.setMixed(true);
        knockedOff.setMixed(true);

        for (int i = 1; i < NUMBER_OF_LOCATIONS + 1; i++) {
            try {
                onBoardLocations.add(new Location(board.getBoardLocation(i)));
            } catch (NoSuchLocationException e) {
                e.printStackTrace();
            }
        }


    }



    public void setName(String name) {
        this.boardName = name;
    }


    public LocationInterface getStartLocation() {
        return start;

    }

    public LocationInterface getEndLocation() {
        return end;

    }

    public LocationInterface getKnockedLocation() {
        return knockedOff;
    }


    public LocationInterface getBoardLocation(int locationNumber) throws NoSuchLocationException {
        if (locationNumber < NUMBER_OF_LOCATIONS + 1 && locationNumber > 0) {
            return onBoardLocations.get(locationNumber - 1);
        } else {
            throw new NoSuchLocationException("location doesn't exist or not on the board");
        }

    }

    //returns the location eg on board location or if it is start or knockedOff
    // don't access this in other classes!
    private LocationInterface getMoveSourceLocation(Colour colour, MoveInterface move) throws NoSuchLocationException {
        LocationInterface loc = new Location();
        if (move.getSourceLocation() == 0) {
            if (knockedOff.canRemovePiece(colour)) {
                return knockedOff;
            } else {
                return start;
            }
        } else if (move.getSourceLocation() < 25) {
            try {
                loc = getBoardLocation(move.getSourceLocation());
            } catch (NoSuchLocationException e) {
                e.printStackTrace();

            }
            return loc;
        } else {
            throw new NoSuchLocationException("Location doesn't exist");
        }
    }


    public boolean canMakeMove(Colour colour, MoveInterface move) {
        boolean temp = false;
        LocationInterface moveSourceLoc = new Location();

        if (knockedOff.numberOfPieces(colour) == 0 || (knockedOff.numberOfPieces(colour) > 0 && move.getSourceLocation() == 0)) {
            try {
                //make this not be a reference
               moveSourceLoc = getMoveSourceLocation(colour, move);
            } catch (NoSuchLocationException e) {
                e.printStackTrace();
            }
            if (moveSourceLoc.canRemovePiece(colour)) {
                try {
                    temp = (move.getSourceLocation() + move.getDiceValue()) >= BoardInterface.NUMBER_OF_LOCATIONS + 1 || getBoardLocation(move.getSourceLocation() + move.getDiceValue()).canAddPiece(colour);
                } catch (NoSuchLocationException e) {
                    e.printStackTrace();
                }
                return temp;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public void makeMove(Colour colour, MoveInterface move) throws IllegalMoveException {
        LocationInterface tempLoc = new Location();
        if (canMakeMove(colour, move)) {
            LocationInterface moveSrcLoc = new Location();
            try {
                moveSrcLoc = getMoveSourceLocation(colour, move);
            } catch (NoSuchLocationException e) {
                e.printStackTrace();
            }
            moveSrcLoc.removePiece(colour);
            if (move.getSourceLocation() + move.getDiceValue() < BoardInterface.NUMBER_OF_LOCATIONS + 1) {
                try {
                    tempLoc = getBoardLocation(move.getSourceLocation() + move.getDiceValue());
                }
                catch (NoSuchLocationException e) {
                    e.printStackTrace();
                }
                if (tempLoc.addPieceGetKnocked(colour) == colour.otherColour()) {
                    knockedOff.addPieceGetKnocked(colour.otherColour());
                }
            } else {
                getEndLocation().addPieceGetKnocked(colour);
            }
        } else {
            throw new IllegalMoveException("Can't make that move, it's illegal");
        }

    }

    public int exhaustingMoves(Colour colour, List<Integer> tempDiceValues,List<Integer> diceValues, int counter, int maxMoves, BoardInterface board) {
            for(MoveInterface move : board.possibleMoves(colour,tempDiceValues)){
                BoardInterface boardClone = board.clone();
                try {
                    boardClone.makeMove(colour, move);
                    counter += 1;
                }
                catch (IllegalMoveException e){
                    e.printStackTrace();
                }
                tempDiceValues.remove(new Integer(move.getDiceValue()));
                if(boardClone.possibleMoves(colour,tempDiceValues).size() > 0){
                    counter = exhaustingMoves(colour,tempDiceValues, diceValues,counter,maxMoves,boardClone);
                }
                else{
                    return counter;
                }
                if(counter == maxMoves) {
                    return maxMoves;
                }
                tempDiceValues = new ArrayList<>(diceValues);
                counter = 1;
            }
            return counter;
        }


    public int maxMovesInTurn(Colour colour, List<Integer> diceValues) {
        int counter = 0;
        List<Integer> tempDiceValues = new ArrayList<>(diceValues);

        if (possibleMoves(colour, tempDiceValues).size() == 0) {
            counter = 0;
        } else {
            counter = exhaustingMoves(colour, tempDiceValues,diceValues, counter, diceValues.size(), this);
        }
        return counter;

    }


    public void takeTurn(Colour colour, TurnInterface turn, List<Integer> diceValues) throws IllegalTurnException {
        //checks if all the dice values in the turn are in the dice values given
        boolean containsValidDiceValues = true;
        int[] diceValuesAsInt = new int[diceValues.size()];
        for (int i = 0; i < diceValues.size(); i++) {
            diceValuesAsInt[i] = diceValues.get(i);
        }

        for (MoveInterface move : turn.getMoves()) {
            int match = 0;
            for (int dieRollValue : diceValuesAsInt) {
                if (dieRollValue == move.getDiceValue()) {
                    match += 1;
                }
            }
            if (match == 0) {
                containsValidDiceValues = false;
            }
        }

        List<Integer> tempDiceValues = new ArrayList<>(diceValues);
        int maxMoves = maxMovesInTurn(colour, diceValues);

        if (!(containsValidDiceValues && maxMoves == turn.getMoves().size())) {
            throw new IllegalTurnException("Can't make the turn as it is invalid");
        } else {
            TurnInterface cloneTurn = new Turn();
            BoardInterface cloneBoard = this.clone();
            int winCount = 0;
            for (MoveInterface move : cloneTurn.getMoves()) {
                try {
                    cloneBoard.makeMove(colour, move);
                    cloneTurn.addMove(move);
                    if (cloneBoard.isWinner(colour)) {
                        winCount += 1;
                        break;
                    }
                } catch (IllegalMoveException e) {
                    e.printStackTrace();
                }
            }
            if (winCount == 0) {
                if (maxMoves != turn.getMoves().size()) {
                    if (turn.getMoves().size() != cloneTurn.getMoves().size())
                        throw new IllegalTurnException("Can't make the turn as it is invalid");
                }
            }


            // make a hash table with source as key and die values as values
            // then compare to see if the value is associated with the key
            for (MoveInterface move : turn.getMoves()) {
                HashMap<Integer, List<Integer>> locAndDieValue = new HashMap<>();
                for (MoveInterface mov : possibleMoves(colour, tempDiceValues)) {
                    locAndDieValue.putIfAbsent(mov.getSourceLocation(), new ArrayList<>());
                    locAndDieValue.get(mov.getSourceLocation()).add(mov.getDiceValue());
                }
                if (locAndDieValue.get(move.getSourceLocation()).contains(move.getDiceValue()) && possibleMoves(colour, tempDiceValues).size() != 0) {
                    try {
                        makeMove(colour, move);
                    } catch (IllegalMoveException e) {
                        e.printStackTrace();
                    }
                    tempDiceValues.remove((Integer) move.getDiceValue());

                    //checking a winner to exit the for loop
                    if (isWinner(colour)) {
                        return;
                    }
                    locAndDieValue = new HashMap<>();
                    if (possibleMoves(colour, tempDiceValues).size() != 0) {
                        for (MoveInterface mov : possibleMoves(colour, tempDiceValues)) {
                            locAndDieValue.putIfAbsent(mov.getSourceLocation(), new ArrayList<>());
                            locAndDieValue.get(mov.getSourceLocation()).add(mov.getDiceValue());
                        }
                    }
                } else {
                    throw new IllegalTurnException("Dice values don't match the turn or there are no possible moves");
                }
            }
        }
    }

    public boolean isWinner(Colour colour) {
        return end.numberOfPieces(colour) == BoardInterface.PIECES_PER_PLAYER;
    }

    public Colour winner() {
        if(isWinner(Colour.values()[0])){
            return Colour.values()[0];
        }
        else if(isWinner(Colour.values()[1])){
            return Colour.values()[1];
        }
        else {
            return null;
        }
    }

    public boolean isValid(){
        int counter = 0;
        for(LocationInterface loc : onBoardLocations){
            if(loc.isValid()){
                counter += 1;
            }
        }
        if(start.isValid() && end.isValid() && knockedOff.isValid()){
            counter += 3;
        }
        return counter == 27;
    }

    public Set<MoveInterface> possibleMovesCheck(Colour colour, List<Integer> diceValues, int source){
        Set<MoveInterface> posMoves = new HashSet<>();

        for(Integer i : diceValues) {
            MoveInterface move = new Move();
            try {
                move.setSourceLocation(source);
                move.setDiceValue(i);
            } catch (NoSuchLocationException | IllegalMoveException e) {
                e.printStackTrace();
            }
            if (canMakeMove(colour, move)) {
                posMoves.add(move);
            }
        }
        return posMoves;
    }

    public Set<MoveInterface> possibleMoves (Colour colour, List < Integer > diceValues){
        //creates copy of the dice values and removes duplicates
        ArrayList<Integer> diceValuesCopy = new ArrayList<>(new LinkedHashSet<>(diceValues));
        if (this.getKnockedLocation().numberOfPieces(colour) != 0) {
            return possibleMovesCheck(colour, diceValuesCopy, 0);
        } else {
            Set<MoveInterface> posOnBoardMoves = new HashSet<>();
            for (int i = 0; i < BoardInterface.NUMBER_OF_LOCATIONS + 1; i++) {
                posOnBoardMoves.addAll(possibleMovesCheck(colour, diceValuesCopy, i));
            }
            return posOnBoardMoves;
        }
    }


    public BoardInterface clone(){
        BoardInterface clonedBoard = new Board(this);
        return clonedBoard;
    }

    public String toString(){
        Colour col1 = Colour.values()[0];
        Colour col2 = Colour.values()[1];
        String cli = "  -----------------------  \n" +
                "Start: " + col1 + ": " + start.numberOfPieces(col1) + " " + col2 + ": " + start.numberOfPieces(col2) + "\n" +
                "KnockedOff: " + col1 + ": " + knockedOff.numberOfPieces(col1) + " " + col2 + ": " + knockedOff.numberOfPieces(col2) + "\n";

        for(LocationInterface loc : onBoardLocations){
            if(loc.getName().length() < 2){
                cli += " ";
            }
            if(loc.numberOfPieces(col1) != 0){
                cli += String.format("%s: %s: %d\n", loc.getName(), col1, loc.numberOfPieces(col1));
            }
            else if(loc.numberOfPieces(col2) != 0){
                cli += String.format("%s: %s: %d\n", loc.getName(), col2, loc.numberOfPieces(col2));
            }
            else {
                cli += String.format("%s: Empty\n", loc.getName());
            }

        }

        cli += "End: " + col1 + ": " + end.numberOfPieces(col1) + " " + col2 + ": " + end.numberOfPieces(col2) + "\n";
        cli += "  ----------------------  \n";
        return cli;
    }

}

package will.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Game implements GameInterface {
    private HashMap<Colour, PlayerInterface> players;
    private int numberOfTurns;
    private DiceInterface gameDice;
    private BoardInterface boardToPlayWith;


    public Game(){
        players = new HashMap<>();
        numberOfTurns = 0;
        boardToPlayWith = new Board();
        gameDice = new Dice();

        PlayerInterface player1 = new HumanConsolePlayer();
        PlayerInterface player2 = new ComputerPlayer();
        setPlayer(Colour.values()[0], player1);
        setPlayer(Colour.values()[1], player2);
        gameDice.roll();

    }


    public static void main(String[] args){
        System.out.println("Welcome to Tabula!");
        boolean exit = false;
        List<Integer> menuItems = Arrays.asList(1,2,3,4,5,6);
        GameInterface game = new Game();

        while(!exit){
            System.out.println("----Main Menu-----");
            System.out.println("1. Set Players");
            System.out.println("2. New game");
            System.out.println("3. Load Game");
            System.out.println("4. Play/Continue game");
            System.out.println("5. Save Game");
            System.out.println("6. Exit Game");
            System.out.println("------------------");
            System.out.println("Enter the number of the instruction that you wish to execute");

            //check and receive the input
            Scanner s = new Scanner(System.in);
            int resp;
            do {
                System.out.println("Please enter an integer in the range 1-6");
                while (!s.hasNextInt()) {
                    System.out.println("That isn't an integer");
                    s.nextLine();
                }
                resp = s.nextInt();
            } while (!menuItems.contains(resp));
            //handles the menu
            switch (resp){
                case 1:

                    //Set Players of the new game created
                    for(int i = 1; i < 3; i++) {
                        System.out.println("Select player " + i + "'s type");
                        System.out.println("1. Human Console Player");
                        System.out.println("2. Human GUI Player");
                        System.out.println("3. Computer Player");

                        Scanner sc = new Scanner(System.in);
                        int playerType;
                        do{
                            System.out.println("Please enter an integer in the range 1-3");
                            while(!sc.hasNextInt()){
                                System.out.println("This isn't an integer");
                                sc.nextLine();
                            }
                            playerType = sc.nextInt();
                        } while (playerType != 1 && playerType != 2 && playerType != 3);

                        PlayerInterface player;

                        if(playerType == 1){
                            player = new HumanConsolePlayer();
                        }
                        else if(playerType == 2){
                            player = new HumanGUIPlayer();
                        }
                        else {
                            player = new ComputerPlayer();
                        }
                        game.setPlayer(Colour.values()[i-1], player);
                    }
                    break;
                case 2:
                    //start a new game
                    game = new Game();
                    break;
                case 3:
                    //Load game
                    Scanner ldGame = new Scanner(System.in);
                    System.out.println("Please enter the name of the file you wish to load including the extension");
                    String fileName = ldGame.next();
                    try {
                        game.loadGame(fileName);
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    //maybe put isValid check here for board
                    break;
                case 4:
                    //Continue or play game
                    try {
                        game.play();
                    }
                    catch (PlayerNotDefinedException e){
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    //Save game
                    Scanner svGame = new Scanner(System.in);
                    System.out.println("Please enter the name of the file you want to save including the extension .txt");
                    String filename = svGame.nextLine();
                    try{
                        game.saveGame(filename);
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    //Exit game
                    exit = true;
                    break;
            }

        }
    }

    public void setPlayer(Colour colour, PlayerInterface player){
        //removing the default selection
        if(players.size() >= 2){
            players.clear();
        }

        players.put(colour,player);
    }

    public Colour getCurrentPlayer(){
        if(numberOfTurns % 2 == 0){
            return Colour.values()[0];
        }
        else{
            return Colour.values()[1];
        }
    }

    public Colour play() throws PlayerNotDefinedException{
        if(players.size() ==2 ){
            while(!boardToPlayWith.isWinner(Colour.values()[0]) && !boardToPlayWith.isWinner(Colour.values()[1])){
                BoardInterface boardClone = boardToPlayWith.clone();
                gameDice.roll();

                List<Integer> currentDiceValues;
                try {
                    currentDiceValues = gameDice.getValues();
                    //add check here to see if there is actually any moves
                    TurnInterface currentTurn = players.get(getCurrentPlayer()).getTurn(getCurrentPlayer(), boardClone, currentDiceValues);
                    if(boardClone.isWinner(getCurrentPlayer())){
                        return getCurrentPlayer();
                    }

                    if(currentTurn.getMoves().size() != 0){
                        boardToPlayWith.takeTurn(getCurrentPlayer(), currentTurn, currentDiceValues);
                        System.out.println(boardToPlayWith.toString());
                    }
                    else{
                        System.out.println("Turn skipped as no possible moves available");
                    }
                    numberOfTurns += 1;
                }
                catch (PauseException e) {
                    System.out.println("Game will now be paused...");
                    return null;
                }
                catch (NotRolledYetException e){
                    e.printStackTrace();
                }
                catch (IllegalTurnException e){
                    if(boardToPlayWith.isWinner(Colour.values()[0]) || !boardToPlayWith.isWinner(Colour.values()[1])){
                        System.out.println("Congratulations! " + boardToPlayWith.winner() + " is the  winner");
                        return getCurrentPlayer();
                    }
                    else {
                        System.out.println("Congratulations! " + getCurrentPlayer().otherColour() + " is the  winner");
                        return getCurrentPlayer().otherColour();
                    }
                }
                gameDice.clear();
            }
            System.out.println("Congratulations! " + getCurrentPlayer().otherColour() + " is the  winner");
            return boardToPlayWith.winner();
        }
        else
        {
            throw new PlayerNotDefinedException("Players/Player hasn't been defined");
        }
    }

    //assuming filename includes the extension and can't overwrite a file if it already exists
    public void saveGame(String filename) throws IOException{
        File file = new File(filename);

        if(!file.isFile() && filename.length() >4 && filename.substring(filename.length() - 4).equals(".txt")){
            List<String> lines = new ArrayList<>();
            //adding the colour and players type to the line
            lines.add(getCurrentPlayer() + " " + players.get(getCurrentPlayer()).getClass().getSimpleName() + " " + getCurrentPlayer().otherColour() + " " + players.get(getCurrentPlayer().otherColour()).getClass().getSimpleName());
            //adding number of counters for each colour for:
            //start
            lines.add(Colour.values()[0] + " " + boardToPlayWith.getStartLocation().numberOfPieces(Colour.values()[0]) + " " + Colour.values()[1] + " " + boardToPlayWith.getStartLocation().numberOfPieces(Colour.values()[1]));
            //end
            lines.add(Colour.values()[0] + " " + boardToPlayWith.getEndLocation().numberOfPieces(Colour.values()[0]) + " " + Colour.values()[1] + " " + boardToPlayWith.getEndLocation().numberOfPieces(Colour.values()[1]));
            //knocked off
            lines.add(Colour.values()[0] + " " + boardToPlayWith.getKnockedLocation().numberOfPieces(Colour.values()[0]) + " " + Colour.values()[1] + " " + boardToPlayWith.getKnockedLocation().numberOfPieces(Colour.values()[1]));

            //onboard positions
            for(int i = 1; i < BoardInterface.NUMBER_OF_LOCATIONS + 1; i++){
                try {
                    lines.add(Colour.values()[0] + " " + boardToPlayWith.getBoardLocation(i).numberOfPieces(Colour.values()[0]) + " " + Colour.values()[1] + " " + boardToPlayWith.getBoardLocation(i).numberOfPieces(Colour.values()[1]));
                }
                catch (NoSuchLocationException e){
                    e.printStackTrace();
                }
            }
            //add the dice values that were present before pausing
            String dieValueStr = "";
            for(DieInterface die : gameDice.getDice()){
                try {
                    dieValueStr += die.getValue() + " ";
                }
                catch (NotRolledYetException e){
                    e.printStackTrace();
                }
            }
            lines.add(dieValueStr);
            //write the lines of text to the txt file
            Files.write(Paths.get(filename ), lines, Charset.forName("UTF-8"));
        }
        else{
            throw new IOException("File already exists with that name or isn't in the correct format, must be .txt file");
        }
    }

    public void loadGame(String filename) throws IOException{
        File f = new File(filename);
        if(f.exists() && filename.substring(filename.length() - 4).equals(".txt")){
            Board bToAddValues = new Board();

            for(int k = 0; k < BoardInterface.PIECES_PER_PLAYER; k++){
                try {
                    bToAddValues.getStartLocation().removePiece(Colour.values()[0]);
                    bToAddValues.getStartLocation().removePiece(Colour.values()[1]);
                }
                catch (IllegalMoveException e){
                    e.printStackTrace();
                }
            }
            //Players and types
            String playerLine = Files.readAllLines(Paths.get(filename)).get(0);
            String[] playerData = playerLine.split(" ");

            for(int j=0; j<4; j+=2){
                //checking the colour
                Colour colour;
                if(playerData[j].equals(Colour.values()[0].toString())){
                    colour = Colour.values()[0];
                }
                else{
                    colour = Colour.values()[1];
                }

                if(playerData[j+1].equals("HumanConsolePlayer")){
                    PlayerInterface player = new HumanConsolePlayer();
                    setPlayer(colour, player);
                }
                else if(playerData[j+1].equals("ComputerPlayer")){
                    PlayerInterface player = new ComputerPlayer();
                    setPlayer(colour, player);
                }
                else{
                    PlayerInterface player = new HumanGUIPlayer();
                    setPlayer(colour, player);
                }
            }

            //Start
            String startLine = Files.readAllLines(Paths.get(filename)).get(1);
            String startData[] = startLine.split(" ");

            for(int i = 0; i < Integer.valueOf(startData[1]); i++) {
                try {
                    bToAddValues.getStartLocation().addPieceGetKnocked(Colour.values()[0]);
                }
                catch (IllegalMoveException e){
                    e.printStackTrace();
                }
            }
            for(int i = 0; i < Integer.valueOf(startData[3]); i++) {
                try {
                    bToAddValues.getStartLocation().addPieceGetKnocked(Colour.values()[1]);
                }
                catch (IllegalMoveException e){
                    e.printStackTrace();
                }
            }

            //End
            String endLine = Files.readAllLines(Paths.get(filename)).get(2);
            String endData[] = endLine.split(" ");

            for(int i = 0; i < Integer.valueOf(endData[1]); i++) {
                try {
                    bToAddValues.getEndLocation().addPieceGetKnocked(Colour.values()[0]);
                }
                catch (IllegalMoveException e){
                    e.printStackTrace();
                }
            }
            for(int i = 0; i < Integer.valueOf(endData[3]); i++) {
                try {
                    bToAddValues.getEndLocation().addPieceGetKnocked(Colour.values()[1]);
                }
                catch (IllegalMoveException e){
                    e.printStackTrace();
                }
            }

            //KnockedOff
            String knockedLine = Files.readAllLines(Paths.get(filename)).get(3);
            String knockedData[] = knockedLine.split(" ");

            for(int i = 0; i < Integer.valueOf(knockedData[1]); i++) {
                try {
                    bToAddValues.getKnockedLocation().addPieceGetKnocked(Colour.values()[0]);
                }
                catch (IllegalMoveException e){
                    e.printStackTrace();
                }
            }
            for(int i = 0; i < Integer.valueOf(knockedData[3]); i++) {
                try {
                    bToAddValues.getKnockedLocation().addPieceGetKnocked(Colour.values()[1]);
                }
                catch (IllegalMoveException e){
                    e.printStackTrace();
                }
            }
            //All of the onBoard locations
            for(int i = 4; i < BoardInterface.NUMBER_OF_LOCATIONS + 4; i++){
                String onBoardLine = Files.readAllLines(Paths.get(filename)).get(i);
                String onBoardData[] = onBoardLine.split(" ");
                for(int j = 0; j < Integer.valueOf(onBoardData[1]); j++) {
                    try {
                        bToAddValues.getBoardLocation(i-3).addPieceGetKnocked(Colour.values()[0]);
                    }
                    catch (IllegalMoveException | NoSuchLocationException e){
                        e.printStackTrace();
                    }
                }
                for(int j = 0; j < Integer.valueOf(onBoardData[3]); j++) {
                    try {
                        bToAddValues.getBoardLocation(i-3).addPieceGetKnocked(Colour.values()[1]);
                    }
                    catch (IllegalMoveException | NoSuchLocationException e){
                        e.printStackTrace();
                    }
                }
            }
            boardToPlayWith = bToAddValues;

            //diceValues
            String diceValues = Files.readAllLines(Paths.get(filename)).get(BoardInterface.NUMBER_OF_LOCATIONS + 4);
            String diceValuesData[] = diceValues.split(" ");
            gameDice.clear();
            int i = 0;
            for(DieInterface die : gameDice.getDice()){
                die.setValue(Integer.valueOf(diceValuesData[i]));
                i++;
            }
        }
        else {
            throw new IOException("File doesn't exist or is in the wrong format");
        }


    }

}

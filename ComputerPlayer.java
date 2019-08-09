package will.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ComputerPlayer implements PlayerInterface {


    public ComputerPlayer() {

    }

    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException {
        System.out.println(colour + "'s turn");

        while(true) {

            List<Integer> currentDiceValuesTwo = new ArrayList<>(diceValues);
            BoardInterface clonedBoard = board.clone();
            List<Integer> currentDiceValues = new ArrayList<>(diceValues);
            TurnInterface turn = new Turn();


            while (clonedBoard.possibleMoves(colour, currentDiceValues).size() > 0) {
                int count = 1;
                ArrayList<MoveInterface> posMoves = new ArrayList<>();
                //generates the possible moves for current state of board and dice values
                for (MoveInterface mov : clonedBoard.possibleMoves(colour, currentDiceValues)) {
                    posMoves.add(mov);
                    count += 1;
                }
                Random rand = new Random();
                int randMove;

                if (count > 2) {
                    randMove = rand.nextInt(count - 2) + 1;
                } else if (count == 1) {
                    randMove = count;
                } else {
                    randMove = count - 1;
                }
                MoveInterface move = new Move();

                try {
                    move.setDiceValue(posMoves.get(randMove - 1).getDiceValue());
                    move.setSourceLocation(posMoves.get(randMove - 1).getSourceLocation());
                    turn.addMove(move);
                    currentDiceValues.remove(new Integer(posMoves.get(randMove - 1).getDiceValue()));
                    clonedBoard.makeMove(colour, move);

                    System.out.println(clonedBoard.toString());
                    if(clonedBoard.isWinner(colour)){
                        board = clonedBoard;
                        return turn;
                    }
                } catch (IllegalTurnException | IllegalMoveException | NoSuchLocationException e) {
                    e.printStackTrace();
                }
            }

           // clone to take the turn on and reset if it doesn't work
            BoardInterface cloneBdForLglTurn = board.clone();
            TurnInterface cloneturn = new Turn();
            for(MoveInterface move: turn.getMoves()){
                try {
                    cloneturn.addMove(move);
                }
                catch (IllegalTurnException e){
                    e.printStackTrace();
                }
            }

            try {
                cloneBdForLglTurn.takeTurn(colour, cloneturn, currentDiceValuesTwo);
                return turn;
            }
            catch (IllegalTurnException e){
                e.printStackTrace();
            }
        }
    }
}
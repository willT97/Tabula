package will.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class HumanConsolePlayer implements PlayerInterface {

    public HumanConsolePlayer(){


    }

    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException{
        System.out.println(colour + "'s turn");
        System.out.println("Press Enter to continue or type any character to pause");

        // if statement to check if you wanna pause and throw exception
        Scanner sc = new Scanner(System.in);
        String a = sc.nextLine();

        if(a.isEmpty()){
            int i = diceValues.size();
            List<Integer> currentDiceValues = new ArrayList<>();
            for(int x : diceValues){
                currentDiceValues.add(x);
            }
            TurnInterface turn = new Turn();

            while(turn.getMoves().size() != i){
                System.out.print(board.toString());
                System.out.print("Dice Values:  ");
                for(int dieValue : currentDiceValues){
                    System.out.print(dieValue + " ");
                }
                System.out.print("\n");

                int count = 1;
                System.out.println("Possible moves:");
                ArrayList<MoveInterface> posMoves= new ArrayList<>();

                for(MoveInterface mov : board.possibleMoves(colour,currentDiceValues)){
                    System.out.println(count + ". Position: " + mov.getSourceLocation() + " Dice Value: " + mov.getDiceValue());
                    posMoves.add(mov);
                    count += 1;
                }

                ArrayList<Integer> numberOfMoves = new ArrayList<>();
                for(int x = 1; x < count; x ++){
                    numberOfMoves.add(x);
                }
                int posType;
                do{
                    System.out.println("Please select a move that you wish to make (1-" + (count - 1) + ")");
                    while(!sc.hasNextInt()){
                        System.out.println("This isn't an integer");
                        sc.nextLine();
                    }
                    posType = sc.nextInt();
                } while (!numberOfMoves.contains(posType));

                MoveInterface move = new Move();

                try {
                    move.setDiceValue(posMoves.get(posType - 1).getDiceValue());
                    move.setSourceLocation(posMoves.get(posType - 1).getSourceLocation());
                    turn.addMove(move);
                    currentDiceValues.remove(new Integer(posMoves.get(posType - 1).getDiceValue()));
                    board.makeMove(colour, move);
                }
                catch(IllegalTurnException | IllegalMoveException | NoSuchLocationException e) {
                    e.printStackTrace();
                }
            }
            return turn;
        }
        else {
            throw new PauseException("game will now be paused");
        }
    }
}

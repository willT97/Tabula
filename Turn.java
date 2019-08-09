package will.main;

import java.util.ArrayList;
import java.util.List;

public class Turn implements TurnInterface {

    private List<MoveInterface> moves;

    public Turn(){
        moves = new ArrayList<>();
    }

    public void addMove(MoveInterface move) throws IllegalTurnException{
        if(moves.size() <4){
            moves.add(move);
        }
        else{
            throw new IllegalTurnException("You already have the maximum amount of moves");
        }
    }

    public List<MoveInterface> getMoves(){
        return moves;
    }
}

package will.main;

import java.util.List;


public class HumanGUIPlayer implements PlayerInterface {

    public HumanGUIPlayer(){

    }

    /**
     * Get from the player the turn that they wish to take
     *
     * @param colour the will.main.Colour they are playing as
     *
     * @param board a clone of the current board state, so that the player can try different moves
     *
     * @param diceValues a list of the dice values the player can use.
     *
     * @return the turn the player wishes to take. It is the will.main.HumanConsolePlayer's responsibility to ensure that the turn is legal, matches the provided diceValues and uses as may of the diceValues as possible.
     *
     * @throws PauseException is only used by human players if they are in the middle of a game and wish to pause the game instead of taking a turn.
     **/
    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException{
        TurnInterface hi = new Turn();
        return hi;

    }
}

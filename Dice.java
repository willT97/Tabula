package will.main;

import java.util.ArrayList;
import java.util.List;


public class Dice implements DiceInterface {
    private DieInterface dice1;
    private DieInterface dice2;
    private List<DieInterface> dice;

    public Dice() {
        dice1 = new Die();
        dice2 = new Die();
        dice = new ArrayList<>();
        dice.add(dice1);
        dice.add(dice2);
    }

    public boolean haveRolled() {
        return dice1.hasRolled() && dice2.hasRolled();
    }

    public void roll() {
        dice1.roll();
        dice2.roll();
    }

    public List<Integer> getValues() throws NotRolledYetException {
        List<Integer> diceValues = new ArrayList<>();

        if (haveRolled()) {
            if (dice1.getValue() == dice2.getValue()) {
                for (int i = 0; i < 4; i++) {
                    diceValues.add(dice1.getValue());
                }
            }
            else {
                diceValues.add(dice1.getValue());
                diceValues.add(dice2.getValue());
            }
            return diceValues;
        } else throw new NotRolledYetException("will.main.Dice haven't been rolled yet");

    }

    public void clear(){
        dice1.clear();
        dice2.clear();
    }

    public List<DieInterface> getDice(){
        return dice;
    }
}

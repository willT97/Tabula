package will.main;

import java.util.Random;


public class Die implements DieInterface {

    private boolean rolled;
    private int dieValue;
    private static final Random rand = new Random();

    public Die(){
        rolled = false;
        dieValue = 0;

    }

    public boolean hasRolled(){
        return rolled;
    }

    public void roll(){
        dieValue = rand.nextInt(DieInterface.NUMBER_OF_SIDES_ON_DIE) + 1;
        rolled = true;
    }

    public int getValue() throws NotRolledYetException{
            if(dieValue == 0){
                throw new NotRolledYetException("will.main.Dice has not been rolled yet in the turn");
            }
            return dieValue;
    }

    public void setValue(int value){
        if(value < DieInterface.NUMBER_OF_SIDES_ON_DIE +1  && value >0) {
            dieValue = value;
            rolled = true;
        }
        else {
            rolled = false;
        }
    }

    public void clear(){
        rolled = false;
        dieValue = 0;
    }

    public void setSeed(long seed){
        rand.setSeed(seed);
    }
}

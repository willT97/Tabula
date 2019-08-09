package will.main;

public class Move implements  MoveInterface{
    private int sourceLocation;
    private int valueOfDie;

    public Move(){
    }

    public void setSourceLocation(int locationNumber) throws NoSuchLocationException {

        if (locationNumber < BoardInterface.NUMBER_OF_LOCATIONS + 1 && locationNumber >= 0) {
            sourceLocation = locationNumber;
        } else {
            throw new NoSuchLocationException("Location number not in the range 0-24");
        }
    }

    public int getSourceLocation(){
        return sourceLocation;
    }

    public void setDiceValue(int diceValue) throws IllegalMoveException{
        if (diceValue > -1 && diceValue < DieInterface.NUMBER_OF_SIDES_ON_DIE + 1) {
            valueOfDie = diceValue;
        }
        else {
            throw new IllegalMoveException("dice value not in range");
        }

    }

    public int getDiceValue(){
        return valueOfDie;
    }
}

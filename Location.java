package will.main;

import java.util.HashMap;

public class Location implements LocationInterface {


    private HashMap<Colour, Integer> numberOfEachColour = new HashMap<>();
    private String name;
    private boolean isMixed;
    private Colour col1 = Colour.values()[0];


    public Location(String locationName){
        name = locationName;
        isMixed = false;
        numberOfEachColour.put(col1, 0);
        numberOfEachColour.put(col1.otherColour(), 0);
    }
    public  Location(){
        isMixed = false;
        numberOfEachColour.put(col1, 0);
        numberOfEachColour.put(col1.otherColour(), 0);
    }

    public Location(LocationInterface loc){
        isMixed = false;
        name = loc.getName();
        numberOfEachColour.put(col1, loc.numberOfPieces(col1));
        numberOfEachColour.put(col1.otherColour(), loc.numberOfPieces(col1.otherColour()));
    }


    public String getName(){
        return name;

    }
    public void setName(String name){
        this.name = name;
    }

    public boolean isMixed(){
        return isMixed;
    }

    public void setMixed(boolean isMixed){
        this.isMixed = isMixed;
    }

    public boolean isEmpty(){
        return numberOfEachColour.get(col1) == 0 && numberOfEachColour.get(col1.otherColour()) == 0;
    }

    public int numberOfPieces(Colour colour){
        return numberOfEachColour.get(colour);
    }

    public boolean canAddPiece(Colour colour) {
        return (numberOfPieces(colour.otherColour()) == 1 && numberOfPieces(colour) == 0) || (isMixed || isEmpty() || (numberOfPieces(colour) > 0) && numberOfPieces(colour.otherColour()) == 0);
    }

    public Colour addPieceGetKnocked(Colour colour) throws IllegalMoveException{
        if(!isMixed() && numberOfPieces(colour.otherColour())>1){
            throw new IllegalMoveException("can't make this move");
        }
        else {
            if(!isMixed() && (canAddPiece(colour) && (numberOfPieces(colour.otherColour()) == 1))){
                    numberOfEachColour.replace(colour, 1);
                    numberOfEachColour.replace(colour.otherColour(), 0);
                    return colour.otherColour();
            }
            else {
                numberOfEachColour.replace(colour, numberOfPieces(colour) +1);
                return null;
            }
        }
    }

    public boolean canRemovePiece(Colour colour){
        return (numberOfPieces(colour)>0);
    }

    public void removePiece(Colour colour) throws IllegalMoveException{
        if(canRemovePiece(colour)){
            int prevAmount = numberOfPieces(colour);
            numberOfEachColour.replace(colour, prevAmount -1);
        }
        else {
            throw new IllegalMoveException("There are no counters to be removed");
        }

    }

    public boolean isValid(){
        return !(!isMixed && (numberOfPieces(col1) > 0 && numberOfPieces(col1.otherColour()) > 0));
    }

}

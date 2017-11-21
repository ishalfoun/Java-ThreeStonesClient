
package ThreeStone;

/**
 *The Slot class defines a Tile that can hace stones placed on it.
 * Slots contain stone objects to determine if a stone has been placed or who placed it.
 * @author Pengkim Sy
 */
public class Slot extends Tile {

    Stone stone;
    
    public Slot(int x, int y) {
        super(x, y);
        stone = null;
    }

    public void placeStone(Stone stone) {
        this.stone = stone;
    }

    public Stone getStone() {
        return stone;
    }

    public void setStone(Stone stone) {
        this.stone = stone;
    }
    
    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public Tile getTile() {
        return this;
    }    
    
    @Override
    public boolean hasStone() {
        return stone != null;
    }
    
    /**
     * the toString checks the type of stone that is in the slot and prints out what kind it is to the cell in the table.
     * @return 
     */
    @Override
    public String toString() {
        if (stone!=null)
            if (stone.getType()==PlayerType.COMPUTER)
                return "[C]";
            else if (stone.getType()==PlayerType.COMPUTER_LASTPLACE)
                return "_C_";
            else
                return "[P]";
        else
            return "[ ]";
    }
}

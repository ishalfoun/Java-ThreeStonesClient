package ThreeStone;

/**
 *The flat class defines a "wall" cell on the board where the user can not place stones.
 * 
 * @author Pengkim Sy
 */
public class Flat extends Tile{

    public Flat(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public Tile getTile() {
        return this;
    }

    @Override
    public boolean hasStone() {
        return false;
    }
    
    /**
     * This is what will be displayed on the board where flats are placed.
     * @return 
     */
    public String toString(){
        return "";
    }
}

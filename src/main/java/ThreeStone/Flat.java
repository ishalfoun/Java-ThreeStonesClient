package ThreeStone;

/**
 * Defines a wall on the board
 * @author Isaak Shalfoun, Roan Chamberlain, Pengkim Sy
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
    
    @Override
    public String toString(){
        return "";
    }
}

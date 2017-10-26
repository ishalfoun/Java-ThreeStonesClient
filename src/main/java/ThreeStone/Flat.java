package ThreeStone;

/**
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
    
    public String toString(){
        return "";
    }
}

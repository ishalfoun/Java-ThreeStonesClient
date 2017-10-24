package ThreeStone;

/**
 *
 * @author KimHyonh
 */
public class Stone extends Tile {
    
    public Stone() {
        this(0, 0);
    }
    public Stone(int x, int y) {
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
        return true;
    }
}

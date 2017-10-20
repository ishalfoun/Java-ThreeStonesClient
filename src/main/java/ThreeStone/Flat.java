package ThreeStone;

/**
 *
 * @author KimHyonh
 */
public class Flat extends Tile{

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public Tile getPosition() {
        return this;
    }    
}

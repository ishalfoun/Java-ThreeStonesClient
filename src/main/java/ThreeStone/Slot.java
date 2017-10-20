package ThreeStone;

/**
 *
 * @author KimHyonh
 */
public class Slot extends Tile {

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public Tile getPosition() {
        return this;
    }
}

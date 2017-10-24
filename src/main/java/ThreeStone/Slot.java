package ThreeStone;

/**
 *
 * @author Pengkim Sy
 */
public class Slot extends Tile {

    Stone stone;
    
    public Slot(int x, int y) {
        super(x, y);
        stone = new Stone(x, y);
    }

    public void placeStone(Stone stone) {
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
    
    public String toString(){
        return("Slot");
    }
}

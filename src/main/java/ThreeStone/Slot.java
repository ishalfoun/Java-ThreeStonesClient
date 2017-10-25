package ThreeStone;

/**
 *
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
    
    public String toString(){
        if (stone!=null)
            return stone.getType().toString()+stone.getX()+","+stone.getY();
        else
            return "S";
    }
}

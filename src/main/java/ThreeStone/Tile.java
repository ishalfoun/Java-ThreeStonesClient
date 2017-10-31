
package ThreeStone;

/**
 * Used to define places on the board.
 * @author Isaak Shalfoun, Roan Chamberlain, Pengkim Sy
 */
public abstract class Tile {
    
    protected int x;
    protected int y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
      
    
    public abstract boolean isPlayable();
    
    public abstract Tile getTile();
    
    public abstract boolean hasStone();
}

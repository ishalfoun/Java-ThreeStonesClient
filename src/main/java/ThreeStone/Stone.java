package ThreeStone;

/**
 *
 * @author KimHyonh
 */
public class Stone extends Tile {
    
    PlayerType type;

    public Stone(PlayerType type) {
        this(0, 0, type);        
    }
    
    public Stone(int x, int y, PlayerType type) {
        super(x, y);
        this.type = type;
    }
    
    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
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
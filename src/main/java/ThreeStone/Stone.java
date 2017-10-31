package ThreeStone;

/**
 * Defines a stone that can have a playertype to be displayed on the board.
 * @author Isaak Shalfoun, Roan Chamberlain, Pengkim Sy
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
    
    @Override
    public String toString(){
        if(type == type.PLAYER){
            return "playerStone";
        }
        if(type == type.COMPUTER){
            return "computerStone";
        }
        return "slot";
    }

}
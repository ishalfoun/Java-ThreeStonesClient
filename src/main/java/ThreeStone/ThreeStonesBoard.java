package ThreeStone;

/**
 *
 * @author KimHyonh
 */
public class ThreeStonesBoard {
    
    private Tile[][] boards;
//    private int size;
    
    public ThreeStonesBoard() {
//       this.size = size;
//       boards = new Tile[this.size][this.size];
    }
    
    public void setTile(Tile[][] tiles) {
        for(int i=0; i<tiles.length; i++) {
            for(int j=0; j<tiles.length; j++) {
                boards[i][j] = tiles[i][j];
            }
        }
    }
    
    public int getSize() {
        int size = boards.length;       
       
        return size;
    }
}

package ThreeStone;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pengkim Sy
 */
public class ThreeStonesBoard {
    
    private Tile[][] board;
    private int size;
    
    public ThreeStonesBoard(int size) {
        this.size = size;
        this.board = new Tile[size][size];
    }
    
    public ThreeStonesBoard(Tile[][] board) {
        this.board = board;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public int getSize() {
        return this.size;
    }
    
    
    public List<Tile> getPlayableSlot(Stone stone) {
        List<Tile> playableSlot = new ArrayList<>();
//        for(int i=0; i<this.board.length; i++) {
//            for(int j=0; j<this.board.length; j++) {
//                if(board[i][j].isPlayable() && !board[i][j].hasStone()) {
//                    playableSlot.add(board[i][j]);
//                }
//            }
//        }
        return playableSlot;
    }
    
    public void placeStone(Stone stone) {
        if(board[stone.getX()][stone.getY()].isPlayable()){
            Slot slot = (Slot) board[stone.getX()][stone.getY()];
            slot.placeStone(stone);
        }
    }
}

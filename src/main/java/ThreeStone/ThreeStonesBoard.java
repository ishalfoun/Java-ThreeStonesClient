package ThreeStone;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pengkim Sy
 */
public class ThreeStonesBoard {
    
    Logger l = Logger.getLogger(ClientGame.class.getName());
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
        if(board[stone.getY()][stone.getX()].isPlayable()){
            Slot slot = (Slot) board[stone.getY()][stone.getX()];
            slot.placeStone(stone);
            board[stone.getY()][stone.getX()] = slot;
       // l.log(Level.INFO, "Placed at "+ stone.getY() + " " + stone.getX());
        //l.log(Level.INFO, "tostring is:" + board[stone.getY()][stone.getX()].toString());
        }
    }

    public void fillBoardFromCSV(String pathToCSV){
        
        BufferedReader br = null;
        String line = " ";
        int index = 0;
        
        
        try{
            br = new BufferedReader(new FileReader(pathToCSV));
            while ((line = br.readLine()) != null) {
                String[] lines = line.split(",");
                System.out.println(lines.length);
                for(int i = 0; i < 11; i++){
                    if(lines[i].equals("f")){
                        board[index][i] = new Flat(index,i);
                    }
                    if(lines[i].equals("s")){
                        board[index][i] = new Slot(index,i);
                    }
                }
                index++;    
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        
    }
}

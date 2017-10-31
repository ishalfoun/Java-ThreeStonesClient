package ThreeStone;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Defines the model for our board.
 * 
 * @author Isaak Shalfoun, Roan Chamberlain, Pengkim Sy
 */
public class ThreeStonesBoard {
    
    Logger l = Logger.getLogger(ClientGame.class.getName());
    private Tile[][] board;
    private int size;
    private Stone lastStone;
    
    /**
     * constuctor to instantiate the size of the board and the 2d array of tiles that represent the board
     * @param size 
     */
    public ThreeStonesBoard(int size) {
        this.size = size;
        this.board = new Tile[size][size];
    }
        
    public ThreeStonesBoard(Tile[][] board) {
        this.board = board;
    }

    /**
     * returns the model
     * @return 
     */
    public Tile[][] getBoard() {
        return board;
    }
/**
 * getter for the size of the board
 * @return 
 */
    public int getSize() {
        return this.size;
    }
        
    /**
     * updates the 2d array with a new stone
     * @param stone 
     */
    public void placeStone(Stone stone) {
        if(board[stone.getY()][stone.getX()].isPlayable()){
            Slot slot = (Slot) board[stone.getY()][stone.getX()];
            
            if (stone.getType()==PlayerType.COMPUTER)
            {
                if (lastStone !=null)
                {
                    lastStone.setType(PlayerType.COMPUTER);
                }
                stone.setType(PlayerType.COMPUTER_LASTPLACE);
                lastStone=stone;
            }
            slot.placeStone(stone);
            board[stone.getY()][stone.getX()] = slot;
            
       // l.log(Level.INFO, "Placed at "+ stone.getY() + " " + stone.getX());
        //l.log(Level.INFO, "tostring is:" + board[stone.getY()][stone.getX()].toString());
        }
    }

    /**
     * loads the 2d tile array based on the values in the csv file that represents the board
     * @param pathToCSV 
     */
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

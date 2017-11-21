
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
 * The ThreeStonesBoard class defines the model that will be displayed in our view
 * @author Pengkim Sy
 */
public class ThreeStonesBoard {
    
    Logger l = Logger.getLogger(ClientGame.class.getName());
    private Tile[][] board;
    private int size;
    private Stone lastStone;
    
    /**
     * Constructor used to define the board.
     * It instanciates a 2d array if tiles representing the games board.
     * @param size the size of the board to be created.
     */
    public ThreeStonesBoard(int size) {
        this.size = size;
        this.board = new Tile[size][size];
    }
    
    /**
     * alternate constructor to create a board based on a 2d array of tiles.
     * @param board 
     */
    public ThreeStonesBoard(Tile[][] board) {
        this.board = board;
    }

    /**
     * getter to return the objects board.
     * @return 
     */
    public Tile[][] getBoard() {
        return board;
    }

    /**
     * getter to retrieve the size of the board
     */
    public int getSize() {
        return this.size;
    }

    
    
    /**
     * the placeStone method is used to update the board model with new stones
     * @param stone stone object that contains where it is to be placed on the board.
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
     * the fillBoardFromCSV method is used to fill the 2d tile array with the appropriate tiles based on a specified csv file. 
     * @param pathToCSV path the the file containing layout of the board before the game is to be played.
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

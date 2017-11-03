/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ThreeStone;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

/**
 *The ClientGame class handles drawing the board using JFrame.
 * It handles retrieving user input and calling the appropriate ClientSession methods to send the users moves to the server and retrieve data from the server.
 * 
 * 
 * 
 * 
 * @author Roan Chamberlain
 */





public class ClientGame {

    ClientSession connection;
    ThreeStonesBoard boardModel;
    Stone stone;
    boolean gameOn = false;
    Stone clientStone;
    JTable table;
    JFrame frame, gameOverframe, menuFrame, boardFrame;
    Stone rememberStone;
    int scoreUser, scoreComp;
    JLabel scoreLabel;
    String scoreText;
    JButton quitGameBtn;
    ArrayList<Object> recvd;
    MouseListener listen;

    Logger l = Logger.getLogger(ClientGame.class.getName());

    /**
     * Instantiates the ClientGame and calls setUpGameBoard and drawMenu.
     * 
     * @throws IOException if a connection could not be made to the server 
     */
    public ClientGame() throws IOException {
        l.log(Level.INFO, "Starting Game");
        listen=new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    sendStone(row, col);
                }
            }
        };
        setUpGameBoard();
        drawMenu();
        // if user clicks on start, the connection is started
    }

    
    /**
     * The setUpGameBoard method instanciates the ThreeStoneBoard object which represents the model for our view.
     * It then fills the board from a csv file which defines the places on the board.
     */
    public void setUpGameBoard() {
        boardModel = new ThreeStonesBoard(11);
        boardModel.fillBoardFromCSV("src/main/resources/board.csv");
    }

    /**
     * Initiates the connection to the server at the ip address specified by the user.
     * 
     * @param server The ip address of the server
     * @throws IOException 
     */
    public void initConnection(String server) throws IOException {
        //String server = "10.230.119.125";
        int servPort = 50000;
        connection = new ClientSession(new Socket(server, servPort));
        l.log(Level.INFO, "connection successful, server=" + server);
    }

    /**
     * Closes the connection with the server when the user does not want to play another game or decides to exit.
     * 
     * @throws IOException 
     */
    public void close() throws IOException
    {
        connection.closeSocket();
        menuFrame.dispose();
        if (boardFrame != null)
        {
            boardFrame.dispose();
        }
        if (gameOverframe !=null)
        {
         gameOverframe.dispose()   ;
        }
    }
    
    /**
     * The drawMenu button displays a menu to the user and gives them the option to start a game or to quit
     */
    public void drawMenu() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        menuFrame = new JFrame();
        menuFrame.setTitle("Three Stones Online");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String server = JOptionPane.showInputDialog(frame, "Enter IP-Address of the Server");
        try {
            initConnection(server);
        } catch (IOException ex) {
            l.log(Level.SEVERE, "Connection Unsuccessful");
        }

        JButton startGameBtn = new JButton("Start Game");
        quitGameBtn = new JButton("Quit");
        startGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // display/center the jdialog when the button is pressed
                drawBoard();

                l.log(Level.INFO, "user clicked to start");
                if (waitForResponse()) {
                    setClickListeners();
                }
                l.log(Level.INFO, "waiting for response error");

            }
        });
        quitGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                l.log(Level.INFO, "user clicked to quit");
                try {
                    close();
                } catch (IOException ex) {
                l.log(Level.SEVERE, "Error while closing socket");
                }
            }
        });
        JPanel panel = new JPanel();
        panel.add(startGameBtn);
        panel.add(quitGameBtn);
        menuFrame.add(panel);
        menuFrame.pack();
        menuFrame.setVisible(true);
    }

    
    /**
     * the restartGame method is used to call the method in the connection object to tell the server that the user wants to play another game.
     */
    public void restartGame() {
        l.log(Level.INFO, "sending ack_play_again");
        try {
            connection.sendPacket(null, Opcode.ACK_PLAY_AGAIN);
        } catch (IOException ex) {
            l.log(Level.INFO, "sending error "+ex);
        }
        if (waitForResponse()) {
            setUpGameBoard();
            drawBoard();
            setClickListeners();
        }
    }

    
    /**
     * the gameOver method will display a jframe to tell the user tat the game has completed.
     * the view will give the user the option to play another game or to quit the game. 
     */
    public void gameOver() {
        l.log(Level.INFO, "Game Over");

        JFrame.setDefaultLookAndFeelDecorated(true);
        gameOverframe = new JFrame();
        gameOverframe.setTitle("Three Stones Online");
        gameOverframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton restartBtn = new JButton("Restart");
        restartBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                l.log(Level.INFO, "user clicked to Restart");
                boardFrame.dispose();
                menuFrame.dispose();
                gameOverframe.dispose();
                restartGame();
            }
        });
        
        JPanel panel = new JPanel();
        String winner;
        if (scoreUser>scoreComp)
        {
            winner = "You won!";
        }else if(scoreComp>scoreUser)
        {
            winner = "The Computer won.";
        }
        else
            winner = "The Game ended in a Draw";
        
        JLabel label = new JLabel("GAME OVER! "+winner + "\nPlay Again?");
        label.setBounds(50, 100, 100, 30);
        panel.add(label);
        panel.add(restartBtn);
        panel.add(quitGameBtn);
        gameOverframe.add(panel);
        gameOverframe.pack();
        gameOverframe.setVisible(true);
    }

    
    /**
     * The waitForResponse method is used to wait for the server to send a packet back to the client once the user has sent a packet.
     * @return 
     */
    public boolean waitForResponse() {
        l.log(Level.INFO, "waiting for ACK_GAME_START");
        disableClickListeners();
        try {
            recvd = connection.receivePacket();
            if (recvd.get(1) == Opcode.ACK_GAME_START) {
                l.log(Level.INFO, "received ACK_GAME_START");
                return true;
            }
        } catch (IOException ex) {
            l.log(Level.SEVERE, "Incorrect Packet received from server"+ ex);
            if (ex.toString().contains("reset"))
                scoreLabel.setText("SERVER HAS CLOSED YOUR CONNECTION");
                
            return false;
        }
        return false;
    }

    public void disableClickListeners() {
        table.removeMouseListener(listen);
    }

    /**
     * the redrawBoard method is used to refresh the view based on the board
     * model. It should be called everytime that the user places a stone or when
     * the server sends back a move.
     */
    public void redrawBoard() {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = boardModel.getBoard()[i][j].toString();
                table.setValueAt(toPrint, i, j);
                scoreText = "User:" + scoreUser + " Server:" + scoreComp;
                scoreLabel.setText(scoreText);
                
            }
        }
    }

    
    /**
     * the redraw board method is used to create the jframe where the game will be played and create the table that will represent the board. 
     * it also draws the whole board.
     * 
     */
    public void drawBoard() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        boardFrame = new JFrame();
        boardFrame.setTitle("Three Stones Online");
        boardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        table = new JTable(boardModel.getSize(), boardModel.getSize());

        
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setRowHeight(55);
        //table.setDefaultRenderer(Color.class, new ColorRenderer(true));

        boardFrame.setPreferredSize(new Dimension(800, 600));
        for (int i = 0; i < 11; i++) {
            //table.getColumnModel().getColumn(i).setCellRenderer(new IconRenderer());
            for (int j = 0; j < 11; j++) {
                String toPrint = boardModel.getBoard()[i][j].toString();
                table.setValueAt(toPrint, i, j);
                System.out.println("i is " + i);
                System.out.println("j is " + j);
                //paintIcon(table, ("Images/computerStone.png"), i, j);
                //table.setValueAt(new ImageIcon("Images/computerStone.png"), i, j);
                
            }
        }
         scoreUser = 0;
         scoreComp =0;
        scoreText = "User:" + scoreUser + " Server:" + scoreComp;
        scoreLabel = new JLabel(scoreText);
        scoreLabel.setBounds(10, 10, 100, 30);
        

        JPanel panel = new JPanel();
        table.setBounds(20, 20, 800, 600);
        panel.add(scoreLabel);
        panel.add(table);
        boardFrame.add(panel);
        boardFrame.pack();
        boardFrame.setVisible(true);
    }

    /**
     * the setClickListeners method sets event handlers on all the cells in the table.
     * 
     * @param table  the board that the user needs to be able to click on.
     */
    public void setClickListeners() {
        table.addMouseListener(listen);
    }

    /**
     * the send stone method gets called from the click listener on the cells and is used to send a message to the server that the user is attempting to make a move.
     * @param row
     * @param col 
     */
    public void sendStone(int row, int col) {
        clientStone = new Stone(col, row, PlayerType.PLAYER);
        try {
            l.log(Level.INFO, "sending stone (" + col + "," + row + ")");
            connection.sendPacket(clientStone, Opcode.CLIENT_PLACE);
        } catch (IOException ex) {
            l.log(Level.INFO, "sending error");
        }
        receiveStone();
    }

    /**
     * the recieveStone method is called after the user places a stone. it will wait for the server to send a packet and update the apporopriate fields in this calss to display the new move anf score.
     *  
     */
    public void receiveStone() {
        try {
            recvd = connection.receivePacket();
            if (recvd.get(1) == Opcode.NOT_VALID_PLACE) {
                l.log(Level.INFO, "NOT VALID MESSAGE DISPLAYED");
                scoreLabel.setText(scoreText+" THAT MOVE WAS NOT VALID. TRY AGAIN");
            }
            if (recvd.get(1) == Opcode.SERVER_PLACE) {
                stone = (Stone) recvd.get(0);
                scoreUser = (int) recvd.get(2);
                scoreComp = (int) recvd.get(3);
                
                l.log(Level.INFO, "RECEIVED SERVER_PLACE(" + stone.getX() + "," + stone.getY() + ") Score=(" + scoreUser + "," + scoreComp + ")");
                scoreText = "User:" + scoreUser + " Server:" + scoreComp;
                scoreLabel.setText(scoreText);
                
                boardModel.placeStone(stone);
                boardModel.placeStone(clientStone);
                rememberStone = stone;
            }
            if (recvd.get(1) == Opcode.REQ_PLAY_AGAIN) {
                l.log(Level.INFO, "received req play again");
                gameOver();
            }
        } catch (IOException ex) {
            l.log(Level.INFO, "receiving unsuccessful "+ex);
        }
        redrawBoard();
    }
}

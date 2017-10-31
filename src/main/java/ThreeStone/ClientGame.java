
package ThreeStone;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * This class handles each game that is played
 * @author Isaak Shalfoun, Roan Chamberlain, Pengkim Sy
 */
public class ClientGame {

    ClientSession connection;
    ThreeStonesBoard boardModel;
    boolean gameOn = false;
    Stone stone;
    Stone clientStone;
    Stone rememberStone;
    int scoreUser, scoreComp;
    String scoreText;
    ArrayList<Object> recvd;
    MouseListener listen; JTable table;
    JFrame frame, gameOverframe, menuFrame, boardFrame;
     JLabel scoreLabel;
   JButton quitGameBtn;
    

    Logger l = Logger.getLogger(ClientGame.class.getName());

    /**
     * The ClientGame class handles displaying the gui and contains a
     * ClientServer object to be able to communicate with the server
     *
     * @author Roan Chamberlain
     * @throws IOException
     */
    public ClientGame() throws IOException {
        l.log(Level.INFO, "Starting Game");
        listen = new java.awt.event.MouseAdapter() {
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
     * Gets called to instantiate the classes ThreeStonesBoard
     * class and loads it based on a csv file containing what the board looks
     * like at the beginning of the game
     *
     */
    public void setUpGameBoard() {
        boardModel = new ThreeStonesBoard(11);
        boardModel.fillBoardFromCSV("src/main/resources/board.csv");
    }

    /**
     * Instantiates the ClientSession Object based on the ip that the user wants
     * to connect to
     *
     * @param server
     * @throws IOException
     */
    public void initConnection(String server) throws IOException {
        int servPort = 50000;
        connection = new ClientSession(new Socket(server, servPort));
        l.log(Level.INFO, "connection successful, server=" + server);
    }

    /**
     * Closes the connection with the server and disposes the
     * javafx when the game is over.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        connection.closeSocket();
        menuFrame.dispose();
        if (boardFrame != null) {
            boardFrame.dispose();
        }
        if (gameOverframe != null) {
            gameOverframe.dispose();
        }
    }

    /**
     * Used to create the jframe menu and display it to the user
     * before it displays the menu, a JOptionPane is displayed to the user to
     * ask for an ip to connect to
     */
    public void drawMenu() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        menuFrame = new JFrame();
        menuFrame.setTitle("Three Stones Online");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String server = JOptionPane.showInputDialog(frame, "Enter IP-Address of the Server");
        if (server == null) {
            System.exit(0);
        }
        try {
            initConnection(server);
        } catch (IOException ex) {
            l.log(Level.SEVERE, "Connection Unsuccessful");
            System.exit(0);
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
     * Used to tell the server that the user wants to play again
     * and to redraw a fresh board
     */
    public void restartGame() {
        l.log(Level.INFO, "sending ack_play_again");
        try {
            connection.sendPacket(null, Opcode.ACK_PLAY_AGAIN);
        } catch (IOException ex) {
            l.log(Level.INFO, "sending error " + ex);
        }
        if (waitForResponse()) {
            setUpGameBoard();
            drawBoard();
            setClickListeners();
        }
    }

    /**
     * Is called when the game has finished. Will display a restart
     * button to the user to ask if they want to play again
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
        if (scoreUser > scoreComp) {
            winner = "You won!";
        } else if (scoreComp > scoreUser) {
            winner = "The Computer won.";
        } else {
            winner = "The Game ended in a Draw";
        }

        JLabel label = new JLabel("GAME OVER! " + winner + " Play Again?");
        label.setBounds(50, 100, 100, 30);
        panel.add(label);
        panel.add(restartBtn);
        panel.add(quitGameBtn);
        gameOverframe.add(panel);
        gameOverframe.pack();
        gameOverframe.setVisible(true);
    }

    /**
     * Waits for server response to start game
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
            l.log(Level.SEVERE, "Incorrect Packet received from server" + ex);
            if (ex.toString().contains("reset")) {
                scoreLabel.setText("SERVER HAS CLOSED YOUR CONNECTION");
            }

            return false;
        }
        return false;
    }

    /**
     * Disables click listeners while waiting for the server response
     */
    public void disableClickListeners() {
        table.removeMouseListener(listen);
    }

    /**
     * Updates the View
     */
    public void redrawBoard() {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = boardModel.getBoard()[i][j].toString();
                table.setValueAt(toPrint, i, j);
            }
        }
    }

    /**
     * Displays the board and the score
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
        boardFrame.setPreferredSize(new Dimension(800, 600));
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = boardModel.getBoard()[i][j].toString();
                table.setValueAt(toPrint, i, j);
            }
        }
        scoreUser = 0;
        scoreComp = 0;
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
     * Sets the click listeners on the table
     */
    public void setClickListeners() {
        table.addMouseListener(listen);
    }

    /**
     * Uses the connections sendStone to send a move to the server
     *
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
     * Uses the connectionObject to receive the servers move
     */
    public void receiveStone() {
        try {
            recvd = connection.receivePacket();
            if (recvd.get(1) == Opcode.NOT_VALID_PLACE) {
                l.log(Level.INFO, "NOT VALID MESSAGE DISPLAYED");
                scoreLabel.setText(scoreText + " THAT MOVE WAS NOT VALID. TRY AGAIN");
            }
            if (recvd.get(1) == Opcode.SERVER_PLACE) {
                placeServer();
            }
            if (recvd.get(1) == Opcode.REQ_PLAY_AGAIN) {
                l.log(Level.INFO, "received req play again");
                placeServer();
                gameOver();
            }
        } catch (IOException ex) {
            l.log(Level.INFO, "receiving unsuccessful " + ex);
        }
        redrawBoard();
    }

    /**
     * Helper method for receiveStone().
     */
    public void placeServer() {

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
}

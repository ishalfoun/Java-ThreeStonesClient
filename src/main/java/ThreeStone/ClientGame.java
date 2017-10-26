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
 *
 * @author 1542745
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

    Logger l = Logger.getLogger(ClientGame.class.getName());

    public ClientGame() throws IOException {
        l.log(Level.INFO, "Starting Game");
        setUpGameBoard();
        drawMenu();
        // if user clicks on start, the connection is started
    }

    public void setUpGameBoard() {
        boardModel = new ThreeStonesBoard(11);
        boardModel.fillBoardFromCSV("src/main/resources/board.csv");
    }

    public void initConnection(String server) throws IOException {
        //String server = "10.230.119.125";
        int servPort = 50000;
        connection = new ClientSession(new Socket(server, servPort));
        l.log(Level.INFO, "connection successful, server=" + server);
    }

    public void close() throws IOException
    {
        connection.closeSocket();
        boardFrame.dispose();
    }
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
                    setClickListeners(table);
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
            setClickListeners(table);
        }
    }

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
        
        JLabel label = new JLabel("GAME OVER! The Winner is: "+/*+(scoreUser>scoreComp)?"User":"Computer"+*/" Play Again?");
        label.setBounds(50, 100, 100, 30);
        panel.add(label);
        panel.add(restartBtn);
        panel.add(quitGameBtn);
        gameOverframe.add(panel);
        gameOverframe.pack();
        gameOverframe.setVisible(true);
    }

    public boolean waitForResponse() {
        l.log(Level.INFO, "waiting for ACK_GAME_START");
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

    public void drawBoard() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        boardFrame = new JFrame();
        boardFrame.setTitle("Three Stones Online");
        boardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        table = new JTable(boardModel.getSize(), boardModel.getSize()){
            @Override
            public Class<?> getColumnClass(int columnIndex){
                return Icon.class;
            }
        };
        
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setRowHeight(55);
        //table.setDefaultRenderer(Color.class, new ColorRenderer(true));

        
        boardFrame.setPreferredSize(new Dimension(800, 600));
        for (int i = 0; i < 11; i++) {
            //table.getColumnModel().getColumn(i).setCellRenderer(new IconRenderer());
            for (int j = 0; j < 11; j++) {
                String toPrint = boardModel.getBoard()[i][j].toString();
                //table.setValueAt(toPrint, i, j);
                //paintIcon(table, ("Images/computerStone.png"), i, j);
                table.setValueAt(new ImageIcon("Images/computerStone.png"), i, j);
                
            }
        }
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

    public void setClickListeners(JTable table) {
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    sendStone(row, col);
                }
            }
        });
    }

    public void sendStone(int row, int col) {
        clientStone = new Stone(col, row, PlayerType.PLAYER);
        l.log(Level.INFO, "sending stone (" + col + "," + row + ")");
        try {
            connection.sendPacket(clientStone, Opcode.CLIENT_PLACE);
        } catch (IOException ex) {
            l.log(Level.INFO, "sending error");
        }
        l.log(Level.INFO, "send successful");
        receiveStone();
    }

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

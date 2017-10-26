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
import javax.swing.JButton;
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
        l.log(Level.INFO, "user initiated connection");

        //String server = "10.230.119.125";
        int servPort = 50000;
        connection = new ClientSession(new Socket(server, servPort));

        l.log(Level.INFO, "init connection successful, server=" + server);
    }

    public void drawMenu() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        menuFrame = new JFrame();
        menuFrame.setTitle("Three Stones Online");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String server = JOptionPane.showInputDialog(frame,"what ip do you want to connect to");
        l.log(Level.INFO, "Drawing menu");
        JButton startGameBtn = new JButton("Start Game");
        startGameBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // display/center the jdialog when the button is pressed
                drawBoard();

                l.log(Level.INFO, "user clicked to start");
                try {
                    initConnection(server);
                    if (waitForResponse()) {
                        setClickListeners(table);
                    }
                } catch (IOException ex) {
                    l.log(Level.INFO, "waiting for response error");
                }
            }
        });
        JPanel panel = new JPanel();
        panel.add(startGameBtn);
        menuFrame.add(panel);

        menuFrame.pack();
        menuFrame.setVisible(true);

    }

    public void restartGame() {
        l.log(Level.INFO, "sending ack_play_again");
        try {
            connection.sendPacket(null, Opcode.ACK_PLAY_AGAIN);
        } catch (IOException ex) {
            l.log(Level.INFO, "sending error");
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
                gameOverframe.dispose();
                restartGame();
            }
        });
        JButton exitBtn = new JButton("Quit");
        restartBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                l.log(Level.INFO, "user clicked on Quit");
               
                //boardFrame.dispose();
            }
        });
        JPanel panel = new JPanel();
        JLabel label = new JLabel("GAME OVER! Play Again?");
        label.setBounds(50, 100, 100, 30);
        panel.add(label);
        panel.add(restartBtn);
        panel.add(exitBtn);
        gameOverframe.add(panel);
        gameOverframe.pack();
        gameOverframe.setVisible(true);

    }

    public boolean waitForResponse() {

        l.log(Level.INFO, "waiting for ACK_GAME_START");
        ArrayList<Object> recvd;
        try {
            recvd = connection.receivePacket();
            if (recvd.get(1) == Opcode.ACK_GAME_START) {

                l.log(Level.INFO, "received ACK_GAME_START");
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, "Incorrect Packet received from server", ex);
            return false;
        }
        return false;
    }

    public void redrawBoard() {

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = boardModel.getBoard()[i][j].toString();
                //JButton spot = new JButton();
                table.setValueAt(toPrint, i, j);
            }
        }

        scoreLabel.setText(scoreText);
        
        //frame.add(table);
        //frame.pack();
        //frame.setVisible(true);
    }

    public void drawBoard() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        boardFrame = new JFrame();
        boardFrame.setTitle("Three Stones Online");
        boardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        table = new JTable(boardModel.getSize(), boardModel.getSize());
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setRowHeight(55);
        table.setDefaultRenderer(Color.class, new ColorRenderer(true));
        boardFrame.setPreferredSize(new Dimension(800,600));
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = boardModel.getBoard()[i][j].toString();
                        table.setValueAt(toPrint, i, j);
            }
        }
        scoreText = "User:"+scoreUser+" Server:"+scoreComp;
        scoreLabel = new JLabel(scoreText);
        scoreLabel.setBounds(10, 10, 100, 30);
        /* JLabel label = new JLabel("GAME OVER! Play Again?");
        
        panel.add(label);
        panel.add(restartBtn);
        panel.add(exitBtn);
        */
        boardFrame.add(scoreLabel);
        boardFrame.add(table);
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
                   // refreshBoard(table, boardModel);
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

        //boardModel.placeStone(stone);
        l.log(Level.INFO, "send successful");

        receiveStone();

    }

    public void receiveStone() {
        ArrayList<Object> recvd;

        try {
            recvd = connection.receivePacket();
            if (recvd.get(1) == Opcode.NOT_VALID_PLACE) {
                l.log(Level.INFO, "NOT VALID MESSAGE DISPLAYED");
            }
            if (recvd.get(1) == Opcode.SERVER_PLACE) {
                stone = (Stone) recvd.get(0);
                scoreUser= (int) recvd.get(2);
                scoreComp= (int) recvd.get(3);
                l.log(Level.INFO, "RECEIVED SERVER_PLACE(" + stone.getX() + "," + stone.getY() + ") Score=("+scoreUser+","+scoreComp+")");
                boardModel.placeStone(stone);
                boardModel.placeStone(clientStone);
                rememberStone = stone;
            }
            if (recvd.get(1) == Opcode.REQ_PLAY_AGAIN) {
                l.log(Level.INFO, "received req play again");
                gameOver();
            }
        } catch (IOException ex) {

            l.log(Level.INFO, "receiving unsuccessful");
        }

        redrawBoard();
    }
    
    
    
}

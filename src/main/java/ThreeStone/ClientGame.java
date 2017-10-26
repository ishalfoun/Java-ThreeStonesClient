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
    JFrame frame;

    Logger l = Logger.getLogger(ClientGame.class.getName());

    public ClientGame() throws IOException {
        l.log(Level.INFO, "Starting Game");

        boardModel = new ThreeStonesBoard(11);
        boardModel.fillBoardFromCSV("src/main/resources/board.csv");
        drawMenu();
        // if user clicks on start, the connection is started
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
        JFrame frame = new JFrame();
        frame.setTitle("Three Stones Online");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String server = JOptionPane.showInputDialog(frame,"what ip do you want to connect to");
        l.log(Level.INFO, "Drawing menu");
        JButton startGameBtn = new JButton("Start Game");
        startGameBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // display/center the jdialog when the button is pressed
                JTable table = drawBoard();

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
        panel.add(startGameBtn);
        frame.add(panel);

        frame.pack();
        frame.setVisible(true);

    }

    public void gameOver() {
        l.log(Level.INFO, "Game Over");
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

    public void redrawBoard()
    {
        
        JTable table = new JTable(boardModel.getSize(), boardModel.getSize());
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = boardModel.getBoard()[i][j].toString();
                //JButton spot = new JButton();
                table.setValueAt(toPrint, i, j);
            }
        }

        frame.add(table);
        frame.pack();
        frame.setVisible(true);
    }
    public JTable drawBoard() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame();
        frame.setTitle("Three Stones Online");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setSize(800,800);
        
        JTable table = new JTable(boardModel.getSize(), boardModel.getSize());
        table.setRowHeight(55);
        table.setDefaultRenderer(Color.class, new ColorRenderer(true));
        frame.setPreferredSize(new Dimension(800,600));
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = boardModel.getBoard()[i][j].toString();
                switch(toPrint){
                    case "flat":
                        table.setValueAt(toPrint, i, j);
                        break;
                    case "slot":
                        table.setValueAt(toPrint, i, j);
                        break;
                    case "playerStone":
                        table.setValueAt(toPrint, i, j);
                        break;
                    case "computerStone":
                        table.setValueAt(toPrint, i, j);
                        break;
                }
            }
        }

        frame.add(table);
        frame.pack();
        frame.setVisible(true);

        return table;
    }

    public void setClickListeners(JTable table) {
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    sendStone(row, col);
                    refreshBoard(table, boardModel);
                }
            }

        });

    }

    public void sendStone(int row, int col) {

        clientStone = new Stone(row, col, PlayerType.PLAYER);
        l.log(Level.INFO, "sending stone x" + row + " y=" + col);
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

        l.log(Level.INFO, "receiving");
        try {
            recvd = connection.receivePacket();
            if (recvd.get(1) == Opcode.NOT_VALID_PLACE) {
                l.log(Level.INFO, "NOT VALID MESSAGE DISPLAYED");
            }
            if (recvd.get(1) == Opcode.SERVER_PLACE) {
                stone = (Stone) recvd.get(0);
                l.log(Level.INFO, "SERVERS STONE TYPE=="+stone.getType());
                boardModel.placeStone(stone);
                boardModel.placeStone(clientStone);
//drawBoard();
                l.log(Level.INFO, "received server_place, placing now");
            }
            if (recvd.get(1) == Opcode.REQ_PLAY_AGAIN) {
                l.log(Level.INFO, "received req play again");
                gameOver();
            }
        } catch (IOException ex) {

            l.log(Level.INFO, "receiving unsuccessful");
        }

redrawBoard();
        l.log(Level.INFO, "end receive");
    }
    
    
    public void refreshBoard(JTable table, ThreeStonesBoard board){
        System.out.println("Refreshed the board");
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = board.getBoard()[i][j].toString();
                switch(toPrint){
                    case "flat":
                        table.setValueAt(toPrint, i, j);
                        break;
                    case "slot":
                        table.setValueAt(toPrint, i, j);
                        break;
                    case "playerStone":
                        table.setValueAt(toPrint, i, j);
                        break;
                    case "computerStone":
                        table.setValueAt(toPrint, i, j);
                        break;
                }

                
            }
        }
        System.out.println("done refreshing the board");
    }
    
    
}

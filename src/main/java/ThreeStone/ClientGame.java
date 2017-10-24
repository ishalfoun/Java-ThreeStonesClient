/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ThreeStone;

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
import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 *
 * @author 1542745
 */
public class ClientGame {

    ClientSession isi;
    ThreeStonesBoard boardModel;
    Stone stone;
    boolean gameOn=false;

    public ClientGame() throws IOException {

        boardModel = new ThreeStonesBoard(11);
        boardModel.fillBoardFromCSV("src/main/resources/board.csv");
        drawMenu();
        initConnection();

    }

    public void initConnection() throws IOException {
        String server = "192.168.12.104";
        int servPort = 7;
        isi = new ClientSession(new Socket(server, servPort));

    }

    public void drawMenu() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame();
        frame.setTitle("OUR AMAZING GAME!!!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton startGameBtn = new JButton("Start Game");
        startGameBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // display/center the jdialog when the button is pressed
                JTable table = drawBoard(boardModel);
                if (waitForResponse()) {
                    setClickListeners(table);
                }
            }
        });
        frame.add(startGameBtn);

        frame.pack();
        frame.setVisible(true);

    }

    public void gameOver()
    {
        System.out.println("    GAME OVER");
    }
    
    public boolean waitForResponse() {
        ArrayList<Object> recvd;
        try {
            recvd = isi.receivePacket();
            if (recvd.get(1) == Opcode.ACK_GAME_START) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, "Incorrect Packet received from server", ex);
            return false;
        }
        return false;
    }

    public JTable drawBoard(ThreeStonesBoard board) {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame();
        frame.setTitle("OUR AMAZING GAME!!!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setSize(800,800);
        
        JTable table = new JTable(board.getSize(), board.getSize());
        table.setRowHeight(55);
        
        frame.setPreferredSize(new Dimension(800,600));
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = board.getBoard()[i][j].toString();
                //JButton spot = new JButton();

                table.setValueAt(toPrint, i, j);
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
                    System.out.println(row + " " + col);
                    sendStone(row, col);
                }
            }

        });

    }

    public void sendStone(int row, int col) {
        Stone stone = new Stone(row, col, PlayerType.PLAYER);
        try {
            isi.sendPacket(stone, Opcode.CLIENT_PLACE);
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Sending failed");
        }

        boardModel.placeStone(stone);
        receiveStone();

    }

    public void receiveStone() {
        ArrayList<Object> recvd;
        try {
            recvd = isi.receivePacket();
            if (recvd.get(1) == Opcode.SERVER_PLACE) {
                stone = (Stone) recvd.get(0);
                boardModel.placeStone(stone);
            }
            if (recvd.get(1) == Opcode.REQ_PLAY_AGAIN)
            {
                gameOver();
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void refreshBoard(JTable table, ThreeStonesBoard board){
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                String toPrint = board.getBoard()[i][j].toString();
                //JButton spot = new JButton();

                table.setValueAt(toPrint, i, j);
            }
        }
    }
}

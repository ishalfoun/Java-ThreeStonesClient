package Main;

import ThreeStone.ThreeStonesBoard;
import javax.swing.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 1542745
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ThreeStonesBoard boardModel = new ThreeStonesBoard(11);
        boardModel.fillBoardFromCSV("src/main/resources/board.csv");
        JTable table = drawBoard(boardModel);
        setClickListeners(table);

    }

    public static JTable drawBoard(ThreeStonesBoard board) {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame();
        frame.setTitle("OUR AMAZING GAME!!!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTable table = new JTable(board.getSize(), board.getSize());

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

    public static void setClickListeners(JTable table) {
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    System.out.println(row + " " + col);

                }
            }

        });

    }
    

}

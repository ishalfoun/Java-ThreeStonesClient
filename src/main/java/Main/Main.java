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
        /*ThreeStonesBoard boardModel = new ThreeStonesBoard(11);
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame();
        frame.setTitle("OUR AMAZING GAME!!!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);*/
        
         ThreeStonesBoard boardModel = new ThreeStonesBoard(11);
         boardModel.fillBoardFromCSV("src/main/resources/board.csv");
         

        
        
        
        
    }
    
}

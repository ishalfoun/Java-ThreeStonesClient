/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ThreeStone;

/**
 * Defines the opcodes that will be used in the packets
 * @author Isaak Shalfoun, Roan Chamberlain, Pengkim Sy
 */
public enum Opcode {
   REQ_GAME_START(0),
   ACK_GAME_START(1),
   CLIENT_PLACE(2),
   SERVER_PLACE(3),
   REQ_PLAY_AGAIN(4),
   ACK_PLAY_AGAIN (5),
   NOT_VALID_PLACE(6);
   
    private final int value;

    private Opcode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }   
}
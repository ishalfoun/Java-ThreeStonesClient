/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ThreeStone;

/**
 *
 * @author KimHyonh
 */
public abstract class Tile {
    
    protected int x;
    protected int y;
    
    public Tile() {
        this(0, 0);
    }

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract boolean isPlayable();
    public abstract Tile getPosition();
}

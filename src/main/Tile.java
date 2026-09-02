package main;

import java.awt.Color;

public class Tile {
    private static final int TILE_SIZE = 16;
    private int value; // 0-9 representing the tile's assigned number
    private boolean selected;
    
    public Tile() {
        this.value = 0; // Default value
        this.selected = false;
    }
    
    public int getTileSize() {
        return TILE_SIZE;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        if (value >= 0 && value <= 9) {
            this.value = value;
        }
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public void clear() {
        this.value = 0;
    }
}

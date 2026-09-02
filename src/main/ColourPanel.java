package main;

import java.awt.Color;

public class ColourPanel {
    private Color[] colorMap;
    
    public ColourPanel() {
        colorMap = new Color[10];
        // Default colors for each ID
        colorMap[0] = Color.GRAY;           // 0 - white (empty)
        colorMap[1] = Color.GREEN;             // 1 - red
        colorMap[2] = Color.BLUE;            // 2 - blue
        colorMap[3] = Color.RED;           // 3 - green
        colorMap[4] = Color.YELLOW;          // 4 - yellow
        colorMap[5] = Color.MAGENTA;         // 5 - magenta
        colorMap[6] = Color.CYAN;            // 6 - cyan
        colorMap[7] = Color.ORANGE;          // 7 - orange
        colorMap[8] = Color.PINK;            // 8 - pink
        colorMap[9] = new Color(128, 0, 128); // 9 - purple
    }
    
    /**
     * Set the color for a specific ID (0-9)
     */
    public void setColor(int id, Color color) {
        if (id >= 0 && id <= 9 && color != null) {
            colorMap[id] = color;
        }
    }
    
    /**
     * Get the color for a specific ID (0-9)
     */
    public Color getColor(int id) {
        if (id >= 0 && id <= 9) {
            return colorMap[id];
        }
        return Color.BLACK; // Default fallback
    }
    
    /**
     * Get all colors
     */
    public Color[] getAllColors() {
        return colorMap;
    }
    
    /**
     * Reset all colors to defaults
     */
    public void resetDefaults() {
        colorMap[0] = Color.GRAY;
        colorMap[1] = Color.RED;
        colorMap[2] = Color.BLUE;
        colorMap[3] = Color.GREEN;
        colorMap[4] = Color.YELLOW;
        colorMap[5] = Color.MAGENTA;
        colorMap[6] = Color.CYAN;
        colorMap[7] = Color.ORANGE;
        colorMap[8] = Color.PINK;
        colorMap[9] = new Color(128, 0, 128);
    }
}

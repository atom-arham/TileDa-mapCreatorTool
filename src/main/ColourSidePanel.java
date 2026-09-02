package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ColourSidePanel extends JPanel {
    
    public MainPanel mainPanel;
    private ColourPanel colourPanel;
    private KeyHandler keyHandler;
    private Grid grid;
    private static final int PANEL_WIDTH = 250;
    private static final int PANEL_HEIGHT = 720;
    private static final int COLOR_BOX_SIZE = 30;
    private static final int SPACING = 10;
    
    public ColourSidePanel(ColourPanel colourPanel, KeyHandler keyHandler, Grid grid) {
        this.colourPanel = colourPanel;
        this.keyHandler = keyHandler;
        this.grid = grid;
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(new Color(40, 40, 40));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Add export button
        addExportButton();
    }
    
    private void addExportButton() {
        JButton exportButton = new JButton("Export Map");
        exportButton.setPreferredSize(new Dimension(PANEL_WIDTH - 2 * SPACING, 40));
        exportButton.setMaximumSize(new Dimension(PANEL_WIDTH - 2 * SPACING, 40));
        exportButton.setAlignmentX(CENTER_ALIGNMENT);
        exportButton.setBackground(new Color(70, 70, 70));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.setBorderPainted(true);
        
        exportButton.addActionListener(e -> {
            try {
                String result = ExportFunction.exportGridWithDialog(grid, this);
                if (result != null) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Map exported to:\n" + result, 
                        "Export Successful", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Error exporting map: " + ex.getMessage(), 
                    "Export Failed", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        
        this.add(exportButton);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        
        drawColorPalette(g2d);
    }
    
    private void drawColorPalette(Graphics2D g2d) {
        int startX = SPACING;
        int startY = SPACING;
        
        // Title
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Color Palette", startX, startY + 20);
        
        int currentY = startY + 40;
        
        // Draw all 10 colors (0-9)
        for (int i = 0; i <= 9; i++) {
            drawColorEntry(g2d, i, startX, currentY);
            currentY += COLOR_BOX_SIZE + SPACING;
        }
    }
    
    private void drawColorEntry(Graphics2D g2d, int colorId, int x, int y) {
        Color color = colourPanel.getColor(colorId);
        String colorName = getColorName(colorId);
        
        // Check if this is the current paint color
        boolean isPaintColor = keyHandler.isPaintModeActive() && keyHandler.getPaintNumber() == colorId;
        
        // Draw color box
        g2d.setColor(color);
        g2d.fillRect(x, y, COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        
        // Draw border
        g2d.setColor(isPaintColor ? new Color(255, 200, 0) : Color.BLACK); // Yellow border for paint color
        g2d.setStroke(new BasicStroke(isPaintColor ? 3 : 2));
        g2d.drawRect(x, y, COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        
        // Draw ID number on the box
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String idStr = String.valueOf(colorId);
        int textX = x + COLOR_BOX_SIZE / 2 - 3;
        int textY = y + COLOR_BOX_SIZE / 2 + 4;
        g2d.drawString(idStr, textX, textY);
        
        // Draw color name and RGB values
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        int textStartX = x + COLOR_BOX_SIZE + SPACING;
        int textStartY = y + 12;
        
        g2d.drawString(colorName, textStartX, textStartY);
        
        // Draw RGB values
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        g2d.setColor(new Color(200, 200, 200));
        String rgbStr = String.format("RGB(%d,%d,%d)", color.getRed(), color.getGreen(), color.getBlue());
        g2d.drawString(rgbStr, textStartX, textStartY + 15);
    }
    
    private String getColorName(int colorId) {
        switch (colorId) {
            case 0: return "Empty (White)";
            case 1: return "Green";
            case 2: return "Blue";
            case 3: return "Red";
            case 4: return "Yellow";
            case 5: return "Magenta";
            case 6: return "Cyan";
            case 7: return "Orange";
            case 8: return "Pink";
            case 9: return "Purple";
            default: return "Unknown";
        }
    }
    
    public static int getPanelWidth() {
        return PANEL_WIDTH;
    }
}

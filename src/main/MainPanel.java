package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class MainPanel extends JPanel implements Runnable {

    private final int TILE_SIZE = 48;
    private final int WINDOW_WIDTH = 1280;
    private final int WINDOW_HEIGHT = 720;
    private Grid grid;
    private final ColourPanel colourPanel;
    private KeyHandler keyHandler;
    private Viewport viewport;
    private final Thread gameThread;
    private volatile boolean running = true;
    private int lastZoomPressState = 0; // Track zoom key state for single press
    
    public MainPanel(Grid grid, ColourPanel colourPanel) {
        this.grid = grid;
        this.colourPanel = colourPanel;
        this.keyHandler = new KeyHandler(grid);
        this.viewport = new Viewport(grid, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Initialize first tile as selected
        grid.selectTile(0, 0);
        
        // Set panel properties to fixed size
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        
        // Add listeners
        this.addKeyListener(keyHandler);
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                handleMouseClick(e);
            }
        });
        
        // Start game loop
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    private void handleMouseClick(java.awt.event.MouseEvent e) {
        //screen coordinates to world coordinates
        int worldX = viewport.screenToWorldX(e.getX());
        int worldY = viewport.screenToWorldY(e.getY());
        
        // tile coordinates
        int tileX = worldX / TILE_SIZE;
        int tileY = worldY / TILE_SIZE;
        
        grid.selectTile(tileX, tileY);
    }
    
    public void setGrid(Grid newGrid) {
        this.grid = newGrid;
        this.keyHandler = new KeyHandler(newGrid);
        this.viewport = new Viewport(newGrid, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT);
        grid.selectTile(0, 0);
        
        this.addKeyListener(keyHandler);
        repaint();
    }
    
    @Override
    public void run() {
        long lastUpdate = System.currentTimeMillis();
        
        while (running) {
            long now = System.currentTimeMillis();
            long deltaTime = now - lastUpdate;
            
            // Update every 50ms
            if (deltaTime >= 50) {
                handleInput();
                repaint();
                lastUpdate = now;
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleInput() {
        // Check if in paint mode (Ctrl + number held)
        boolean paintMode = keyHandler.isPaintModeActive();
        int paintNumber = keyHandler.getPaintNumber();
        
        // Handle navigation with WASD or Arrow keys
        if (keyHandler.isUpPressed() || keyHandler.isArrowUpPressed()) {
            if (paintMode) {
                // Paint current tile and move up
                grid.setTileValue(grid.getSelectedX(), grid.getSelectedY(), paintNumber);
            }
            grid.moveSelection(0, -1);
        }
        if (keyHandler.isDownPressed() || keyHandler.isArrowDownPressed()) {
            if (paintMode) {
                // Paint current tile and move down
                grid.setTileValue(grid.getSelectedX(), grid.getSelectedY(), paintNumber);
            }
            grid.moveSelection(0, 1);
        }
        if (keyHandler.isLeftPressed() || keyHandler.isArrowLeftPressed()) {
            if (paintMode) {
                // Paint current tile and move left
                grid.setTileValue(grid.getSelectedX(), grid.getSelectedY(), paintNumber);
            }
            grid.moveSelection(-1, 0);
        }
        if (keyHandler.isRightPressed() || keyHandler.isArrowRightPressed()) {
            if (paintMode) {
                // Paint current tile and move right
                grid.setTileValue(grid.getSelectedX(), grid.getSelectedY(), paintNumber);
            }
            grid.moveSelection(1, 0);
        }
        
        // Handle camera movement (Shift + Arrow keys)
        if (keyHandler.isCameraLeftPressed()) {
            viewport.moveCamera(-1, 0);
        }
        if (keyHandler.isCameraRightPressed()) {
            viewport.moveCamera(1, 0);
        }
        if (keyHandler.isCameraUpPressed()) {
            viewport.moveCamera(0, -1);
        }
        if (keyHandler.isCameraDownPressed()) {
            viewport.moveCamera(0, 1);
        }
        
        // Handle zoom controls
        if (keyHandler.isZoomInPressed()) {
            if (lastZoomPressState == 0) {
                viewport.zoomIn();
                lastZoomPressState = 1;
            }
        } else {
            lastZoomPressState = 0;
        }
        
        if (keyHandler.isZoomOutPressed()) {
            if (lastZoomPressState == 0) {
                viewport.zoomOut();
                lastZoomPressState = 2;
            }
        } else if (lastZoomPressState == 2) {
            lastZoomPressState = 0;
        }
        
        // Handle reset view
        if (keyHandler.isResetViewPressed()) {
            viewport.reset();
        }
        
        // Handle number assignment
        int numberPressed = keyHandler.getPressedNumberOnce();
        if (numberPressed >= 0) {
            grid.setTileValue(grid.getSelectedX(), grid.getSelectedY(), numberPressed);
        }
        
        // Handle export (Ctrl+S)
        if (keyHandler.isExportPressed()) {
            try {
                String filepath = ExportFunction.exportGridWithDialog(grid, this);
                if (filepath != null) {
                    System.out.println("Grid exported to: " + filepath);
                }
            } catch (Exception e) {
                System.err.println("Error exporting grid: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        
        drawGrid(g2d);
        drawInfo(g2d);
    }
    
    private void drawGrid(Graphics2D g2d) {
        float scaledTileSize = viewport.getScaledTileSize();
        float cameraX = viewport.getCameraX();
        float cameraY = viewport.getCameraY();
        
        // Calculate which tiles are visible
        int startTileX = (int) (cameraX / TILE_SIZE);
        int startTileY = (int) (cameraY / TILE_SIZE);
        int endTileX = startTileX + (int) (WINDOW_WIDTH / scaledTileSize) + 2;
        int endTileY = startTileY + (int) (WINDOW_HEIGHT / scaledTileSize) + 2;
        
        // Clamp to grid bounds
        startTileX = Math.max(0, startTileX);
        startTileY = Math.max(0, startTileY);
        endTileX = Math.min(grid.getWidth(), endTileX);
        endTileY = Math.min(grid.getHeight(), endTileY);
        
        // Only draw visible tiles
        for (int y = startTileY; y < endTileY; y++) {
            for (int x = startTileX; x < endTileX; x++) {
                Tile tile = grid.getTile(x, y);
                if (tile != null) {
                    drawTile(g2d, x, y, tile, scaledTileSize);
                }
            }
        }
    }
    
    private void drawTile(Graphics2D g2d, int x, int y, Tile tile, float scaledTileSize) {
        // Convert world coordinates to screen coordinates
        int screenX = viewport.worldToScreenX(x * TILE_SIZE);
        int screenY = viewport.worldToScreenY(y * TILE_SIZE);
        int size = (int) scaledTileSize;
        
        // Get tile color based on value
        Color tileColor = colourPanel.getColor(tile.getValue());
        
        // Highlight if selected
        if (tile.isSelected()) {
            g2d.setColor(new Color(Math.max(0, tileColor.getRed() - 50),
                                   Math.max(0, tileColor.getGreen() - 50),
                                   Math.max(0, tileColor.getBlue() - 50)));
        } else {
            g2d.setColor(tileColor);
        }
        
        // Draw filled tile
        g2d.fillRect(screenX, screenY, size, size);
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(screenX, screenY, size, size);
        
        // Draw value if not 0
        if (tile.getValue() != 0) {
            g2d.setColor(Color.BLACK);
            float fontSize = Math.max(8, scaledTileSize / 5);
            g2d.setFont(new Font("Arial", Font.BOLD, (int) fontSize));
            String valueStr = String.valueOf(tile.getValue());
            int textX = screenX + size / 2 - 3;
            int textY = screenY + size / 2 + 4;
            g2d.drawString(valueStr, textX, textY);
        }
    }
    
    public void drawInfo(Graphics2D g2d) {
        // Check paint mode status
        boolean paintMode = keyHandler.isPaintModeActive();
        int paintNumber = keyHandler.getPaintNumber();
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Status line 1
        String statusLine1 = "Grid: " + grid.getWidth() + "x" + grid.getHeight() +
                      " | Selected: (" + grid.getSelectedX() + ", " + grid.getSelectedY() + ")" +
                      " | Zoom: " + String.format("%.1f", viewport.getZoomLevel()) + "x";
        
        // Paint mode indicator
        if (paintMode) {
            statusLine1 += " | PAINT MODE: " + paintNumber;
            g2d.setColor(new Color(255, 200, 0)); // Yellow for paint mode
        }
        
        g2d.drawString(statusLine1, 10, WINDOW_HEIGHT - 40);
        
        // Status line 2 - Controls
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString("WASD/Arrows: Navigate | 0-9: Assign | Ctrl+Number(0-9)+(Arrow/WASD): Paint | Shift+Arrows: Pan | +/-: Zoom | Shift+R: Reset", 
                      10, WINDOW_HEIGHT - 20);
    }

    public void stopGame() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public KeyHandler getKeyHandler() {
        return keyHandler;
    }
}

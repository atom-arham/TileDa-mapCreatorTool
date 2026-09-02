package main;

public class Grid {
    private Tile[][] tiles;
    private int width;  // x dimension
    private int height; // y dimension
    private int selectedX = 0;
    private int selectedY = 0;
    
    /**
     * Creates a rectangular grid of tiles
     * @param width The width of the grid (x dimension)
     * @param height The height of the grid (y dimension)
     */
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
        
        // Initialize all tiles
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile();
            }
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public Tile getTile(int x, int y) {
        if (isValidPosition(x, y)) {
            return tiles[y][x];
        }
        return null;
    }
    
    public void setTileValue(int x, int y, int value) {
        if (isValidPosition(x, y)) {
            tiles[y][x].setValue(value);
        }
    }
    
    public int getTileValue(int x, int y) {
        if (isValidPosition(x, y)) {
            return tiles[y][x].getValue();
        }
        return -1;
    }
    
    public void clearTile(int x, int y) {
        if (isValidPosition(x, y)) {
            tiles[y][x].clear();
        }
    }
    
    public void selectTile(int x, int y) {
        if (isValidPosition(x, y)) {
            // Deselect previous tile
            tiles[selectedY][selectedX].setSelected(false);
            // Select new tile
            selectedX = x;
            selectedY = y;
            tiles[y][x].setSelected(true);
        }
    }
    
    public int getSelectedX() {
        return selectedX;
    }
    
    public int getSelectedY() {
        return selectedY;
    }
    
    public void moveSelection(int dx, int dy) {
        int newX = selectedX + dx;
        int newY = selectedY + dy;
        selectTile(newX, newY);
    }
    
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    public void clearAll() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x].clear();
            }
        }
    }
    
    public int[][] getGridData() {
        int[][] data = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[y][x] = tiles[y][x].getValue();
            }
        }
        return data;
    }
}

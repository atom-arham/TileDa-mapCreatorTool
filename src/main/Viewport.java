package main;

/**
 * Viewport class handles camera positioning and zoom for grid display
 */
public class Viewport {
    private float cameraX;        // Camera offset X
    private float cameraY;        // Camera offset Y
    private float zoomLevel;      // Zoom level (1.0 = normal, 2.0 = 2x zoom, etc.)
    private int viewportWidth;    // Window width
    private int viewportHeight;   // Window height
    private Grid grid;
    private int tileSize;
    
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 4.0f;
    private static final float ZOOM_STEP = 0.1f;
    private static final int CAMERA_SPEED = 32; // pixels per movement
    
    public Viewport(Grid grid, int tileSize, int viewportWidth, int viewportHeight) {
        this.grid = grid;
        this.tileSize = tileSize;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.cameraX = 0;
        this.cameraY = 0;
        this.zoomLevel = 1.0f;
    }
    
    /**
     * Move camera by offset (with Shift + Arrow keys)
     */
    public void moveCamera(int dx, int dy) {
        int gridPixelWidth = grid.getWidth() * tileSize;
        int gridPixelHeight = grid.getHeight() * tileSize;
        
        // Scale movement by zoom level
        float scaledSpeed = CAMERA_SPEED / zoomLevel;
        
        // Update camera position
        cameraX += dx * scaledSpeed;
        cameraY += dy * scaledSpeed;
        
        // Constrain camera to grid bounds
        constrainCamera(gridPixelWidth, gridPixelHeight);
    }
    
    /**
     * Zoom in (increases zoom level)
     */
    public void zoomIn() {
        float newZoom = Math.min(zoomLevel + ZOOM_STEP, MAX_ZOOM);
        setZoom(newZoom);
    }
    
    /**
     * Zoom out (decreases zoom level)
     */
    public void zoomOut() {
        float newZoom = Math.max(zoomLevel - ZOOM_STEP, MIN_ZOOM);
        setZoom(newZoom);
    }
    
    /**
     * Set zoom level directly
     */
    public void setZoom(float zoom) {
        if (zoom >= MIN_ZOOM && zoom <= MAX_ZOOM) {
            this.zoomLevel = zoom;
            int gridPixelWidth = grid.getWidth() * tileSize;
            int gridPixelHeight = grid.getHeight() * tileSize;
            constrainCamera(gridPixelWidth, gridPixelHeight);
        }
    }
    
    /**
     * Reset zoom and camera to defaults
     */
    public void reset() {
        cameraX = 0;
        cameraY = 0;
        zoomLevel = 1.0f;
    }
    
    /**
     * Constrain camera so viewport doesn't go beyond grid bounds
     */
    private void constrainCamera(int gridPixelWidth, int gridPixelHeight) {
        float scaledViewportWidth = viewportWidth / zoomLevel;
        float scaledViewportHeight = viewportHeight / zoomLevel;
        
        // Constrain X
        if (scaledViewportWidth < gridPixelWidth) {
            cameraX = Math.max(0, Math.min(cameraX, gridPixelWidth - scaledViewportWidth));
        } else {
            cameraX = 0;
        }
        
        // Constrain Y
        if (scaledViewportHeight < gridPixelHeight) {
            cameraY = Math.max(0, Math.min(cameraY, gridPixelHeight - scaledViewportHeight));
        } else {
            cameraY = 0;
        }
    }
    
    /**
     * Convert screen coordinates to world coordinates
     */
    public int screenToWorldX(int screenX) {
        return (int) ((screenX / zoomLevel) + cameraX);
    }
    
    public int screenToWorldY(int screenY) {
        return (int) ((screenY / zoomLevel) + cameraY);
    }
    
    /**
     * Convert world coordinates to screen coordinates
     */
    public int worldToScreenX(int worldX) {
        return (int) ((worldX - cameraX) * zoomLevel);
    }
    
    public int worldToScreenY(int worldY) {
        return (int) ((worldY - cameraY) * zoomLevel);
    }
    
    // Getters
    public float getCameraX() {
        return cameraX;
    }
    
    public float getCameraY() {
        return cameraY;
    }
    
    public float getZoomLevel() {
        return zoomLevel;
    }
    
    public float getScaledTileSize() {
        return tileSize * zoomLevel;
    }
    
    public void updateViewportSize(int newWidth, int newHeight) {
        this.viewportWidth = newWidth;
        this.viewportHeight = newHeight;
    }
}

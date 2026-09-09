package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class GridPanel extends JPanel {
    public enum Tool {
        PAINT_BRUSH,
        FILL_CANVAS,
        RECTANGLE_FILL
    }

    private static final int TILE_SIZE = 48;
    private final TileMap map;
    private final ZoomInOut zoom = new ZoomInOut();
    private BufferedImage selectedImage;
    private int selectedAssetId;
    private Tool tool = Tool.PAINT_BRUSH;
    private int brushSize = 1;
    private int viewportTiles = 16;
    private int selectionStartX = -1;
    private int selectionStartY = -1;
    private int selectionEndX = -1;
    private int selectionEndY = -1;

    public GridPanel(TileMap map) {
        this.map = map;
        setBackground(Color.WHITE);
        setFocusable(true);
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                requestFocusInWindow();
                int[] cell = cellAt(event.getX(), event.getY());
                if (cell == null) {
                    return;
                }
                if (tool == Tool.RECTANGLE_FILL) {
                    selectionStartX = cell[0];
                    selectionStartY = cell[1];
                    selectionEndX = cell[0];
                    selectionEndY = cell[1];
                    repaint();
                } else {
                    applyTool(cell[0], cell[1]);
                }
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                int[] cell = cellAt(event.getX(), event.getY());
                if (cell == null) {
                    return;
                }
                if (tool == Tool.RECTANGLE_FILL) {
                    selectionEndX = cell[0];
                    selectionEndY = cell[1];
                    repaint();
                } else {
                    applyTool(cell[0], cell[1]);
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (tool != Tool.RECTANGLE_FILL || selectionStartX < 0) {
                    return;
                }
                int[] cell = cellAt(event.getX(), event.getY());
                if (cell != null) {
                    selectionEndX = cell[0];
                    selectionEndY = cell[1];
                }
                fillSelection();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                if (event.getWheelRotation() < 0) {
                    zoom.zoomIn();
                } else {
                    zoom.zoomOut();
                }
                updateSize();
            }

        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
        updateSize();
    }

    public void setSelectedImage(BufferedImage image) {
        selectedImage = image;
        selectedAssetId = 0;
    }

    public void setSelectedAsset(AssetManager.Asset asset) {
        selectedImage = asset == null ? null : asset.getImage();
        selectedAssetId = asset == null ? 0 : asset.getId();
    }

    public void setTool(Tool tool) {
        this.tool = tool;
        clearSelection();
    }

    public Tool getTool() {
        return tool;
    }

    public void setBrushSize(int brushSize) {
        if (brushSize < 1 || brushSize > 3) {
            throw new IllegalArgumentException("Brush size must be between 1 and 3");
        }
        this.brushSize = brushSize;
    }

    public void setViewportTiles(int viewportTiles) {
        if (viewportTiles < 16 || viewportTiles > 256 || (viewportTiles & (viewportTiles - 1)) != 0) {
            throw new IllegalArgumentException("Viewport must be a power of two from 16 to 256 tiles");
        }
        this.viewportTiles = viewportTiles;
        updateSize();
    }

    public void applyTool(int x, int y) {
        if (tool == Tool.FILL_CANVAS) {
            fillCanvas();
        } else if (tool == Tool.PAINT_BRUSH) {
            paintTile(x, y);
        }
    }

    public int getBaseTileSize() {
        return TILE_SIZE;
    }

    private void fillCanvas() {
        if (selectedImage == null) {
            return;
        }
        int visibleWidth = Math.min(viewportTiles, map.getWidth());
        int visibleHeight = Math.min(viewportTiles, map.getHeight());
        for (int y = 0; y < visibleHeight; y++) {
            for (int x = 0; x < visibleWidth; x++) {
                map.setTile(x, y, new Tile(selectedImage, selectedAssetId));
            }
        }
        repaint();
    }

    private void fillSelection() {
        if (selectedImage == null || selectionStartX < 0) {
            clearSelection();
            return;
        }
        int left = Math.min(selectionStartX, selectionEndX);
        int right = Math.max(selectionStartX, selectionEndX);
        int top = Math.min(selectionStartY, selectionEndY);
        int bottom = Math.max(selectionStartY, selectionEndY);
        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                map.setTile(x, y, new Tile(selectedImage, selectedAssetId));
            }
        }
        clearSelection();
    }

    private void clearSelection() {
        selectionStartX = -1;
        selectionStartY = -1;
        selectionEndX = -1;
        selectionEndY = -1;
        repaint();
    }

    public final void updateSize() {
        int tileSize = zoom.scale(TILE_SIZE);
        setPreferredSize(new Dimension(viewportTiles * tileSize + 1, viewportTiles * tileSize + 1));
        revalidate();
        repaint();
    }

    int[] cellAt(int pixelX, int pixelY) {
        int tileSize = zoom.scale(TILE_SIZE);
        int x = pixelX / tileSize;
        int y = pixelY / tileSize;
        if (x < 0 || x >= Math.min(viewportTiles, map.getWidth())
            || y < 0 || y >= Math.min(viewportTiles, map.getHeight())) {
            return null;
        }
        return new int[] { x, y };
    }

    void paintTile(int x, int y) {
        if (selectedImage == null) {
            return;
        }
        int startX = x - (brushSize - 1) / 2;
        int startY = y - (brushSize - 1) / 2;
        for (int brushY = 0; brushY < brushSize; brushY++) {
            for (int brushX = 0; brushX < brushSize; brushX++) {
                int tileX = startX + brushX;
                int tileY = startY + brushY;
                if (tileX >= 0 && tileX < Math.min(viewportTiles, map.getWidth())
                    && tileY >= 0 && tileY < Math.min(viewportTiles, map.getHeight())) {
                    map.setTile(tileX, tileY, new Tile(selectedImage, selectedAssetId));
                }
            }
        }
        int tileSize = zoom.scale(TILE_SIZE);
        repaint(Math.max(0, startX) * tileSize, Math.max(0, startY) * tileSize,
                brushSize * tileSize + 1, brushSize * tileSize + 1);
    }

    public void zoomIn() {
        zoom.zoomIn();
        updateSize();
    }

    public void zoomOut() {
        zoom.zoomOut();
        updateSize();
    }

    public void resetZoom() {
        zoom.reset();
        updateSize();
    }

    public void resizeGrid(int size) {
        map.resize(size, size);
        viewportTiles = size;
        updateSize();
    }

    public void refreshAfterLoad() {
        viewportTiles = map.getWidth();
        updateSize();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int tileSize = zoom.scale(TILE_SIZE);
        int visibleWidth = map.getWidth();
        int visibleHeight = map.getHeight();
        for (int y = 0; y < visibleHeight; y++) {
            for (int x = 0; x < visibleWidth; x++) {
                int pixelX = x * tileSize;
                int pixelY = y * tileSize;
                graphics.setColor((x + y) % 2 == 0 ? new Color(238, 241, 244) : Color.WHITE);
                graphics.fillRect(pixelX, pixelY, tileSize, tileSize);
                Tile tile = map.getTile(x, y);
                if (tile != null && tile.getImage() != null) {
                    graphics.drawImage(tile.getImage().getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH),
                            pixelX, pixelY, null);
                }
                graphics.setColor(new Color(210, 214, 218));
                graphics.drawRect(pixelX, pixelY, tileSize, tileSize);
            }
        }
        if (selectionStartX >= 0 && selectionEndX >= 0) {
            int left = Math.min(selectionStartX, selectionEndX);
            int right = Math.max(selectionStartX, selectionEndX);
            int top = Math.min(selectionStartY, selectionEndY);
            int bottom = Math.max(selectionStartY, selectionEndY);
            graphics.setColor(new Color(45, 120, 220, 90));
            graphics.fillRect(left * tileSize, top * tileSize,
                    (right - left + 1) * tileSize, (bottom - top + 1) * tileSize);
            graphics.setColor(new Color(25, 85, 170));
            graphics.drawRect(left * tileSize, top * tileSize,
                    (right - left + 1) * tileSize - 1, (bottom - top + 1) * tileSize - 1);
        }
    }
}
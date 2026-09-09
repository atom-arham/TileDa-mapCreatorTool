package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class MapExporter {
    private static final int TILE_SIZE = 48;

    private MapExporter() { }

    public static void exportPng(File file, TileMap map) throws IOException {
        BufferedImage image = new BufferedImage(
                map.getWidth() * TILE_SIZE,
                map.getHeight() * TILE_SIZE,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    Tile tile = map.getTile(x, y);
                    if (tile != null && tile.getImage() != null) {
                        graphics.drawImage(tile.getImage(), x * TILE_SIZE, y * TILE_SIZE,
                                TILE_SIZE, TILE_SIZE, null);
                    }
                }
            }
        } finally {
            graphics.dispose();
        }
        if (!ImageIO.write(image, "png", file)) {
            throw new IOException("PNG format is not available.");
        }
    }

    public static void exportJson(File file, TileMap map) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("{\n");
            writer.write("  \"width\": " + map.getWidth() + ",\n");
            writer.write("  \"height\": " + map.getHeight() + ",\n");
            writer.write("  \"tiles\": [\n");
            for (int y = 0; y < map.getHeight(); y++) {
                writer.write("    [");
                for (int x = 0; x < map.getWidth(); x++) {
                    Tile tile = map.getTile(x, y);
                    writer.write(String.valueOf(tile == null ? 0 : tile.getAssetId()));
                    if (x < map.getWidth() - 1) writer.write(", ");
                }
                writer.write("]");
                if (y < map.getHeight() - 1) writer.write(",");
                writer.write("\n");
            }
            writer.write("  ]\n");
            writer.write("}\n");
        }
    }
}

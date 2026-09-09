package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public final class ProjectFile {
	private static final String MAGIC = "TILEDA_PROJECT";
	private static final int VERSION = 1;

    private ProjectFile() { }

    public static void save(File file, TileMap map, AssetManager assetManager) throws IOException {
        try (DataOutputStream writer = new DataOutputStream(
                new BufferedOutputStream(new java.io.FileOutputStream(file)))) {
            writer.writeUTF(MAGIC);
            writer.writeInt(VERSION);
            writer.writeInt(assetManager.getFolders().size());
            for (AssetManager.Folder folder : assetManager.getFolders()) {
                writer.writeUTF(folder.getDirectory().getCanonicalPath());
            }
            writer.writeInt(map.getWidth());
            writer.writeInt(map.getHeight());
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    Tile tile = map.getTile(x, y);
                    writer.writeInt(tile == null ? 0 : tile.getAssetId());
                }
            }
        }
    }

    public static void load(File file, TileMap map, AssetManager assetManager) throws IOException {
        try (DataInputStream reader = new DataInputStream(
                new BufferedInputStream(new java.io.FileInputStream(file)))) {
            if (!MAGIC.equals(reader.readUTF()) || VERSION != reader.readInt()) {
                throw new IOException("Unsupported or invalid TileDa project file.");
            }

            int folderCount = reader.readInt();
            if (folderCount < 0) throw new IOException("Invalid folder count.");
            String[] folderPaths = new String[folderCount];
            for (int index = 0; index < folderCount; index++) {
                folderPaths[index] = reader.readUTF();
            }

            int width = reader.readInt();
            int height = reader.readInt();
            if (width < 16 || width > 256 || height < 16 || height > 256) {
                throw new IOException("Project dimensions must be between 16 and 256.");
            }

            int[][] assetIds = new int[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    assetIds[y][x] = reader.readInt();
                }
            }

            assetManager.clear();
            for (String folderPath : folderPaths) {
                File folder = new File(folderPath);
                if (folder.isDirectory()) assetManager.addFolder(folder);
            }
            map.resize(width, height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int assetId = assetIds[y][x];
                    AssetManager.Asset asset = assetManager.get(assetId);
                    map.setTile(x, y, asset == null ? null : new Tile(asset.getImage(), assetId));
                }
            }
        }
    }
}
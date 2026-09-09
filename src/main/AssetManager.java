package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class AssetManager {
	public interface Listener { void assetsChanged(); }

	public static final class Asset {
		private final int id;
		private final File file;
		private final BufferedImage image;
		private final Folder folder;

		private Asset(int id, File file, BufferedImage image, Folder folder) {
			this.id = id;
			this.file = file;
			this.image = image;
			this.folder = folder;
		}

		public int getId() { return id; }
		public File getFile() { return file; }
		public BufferedImage getImage() { return image; }
		public String getName() { return file.getName(); }
		public Folder getFolder() { return folder; }
	}

	public static final class Folder {
		private final File directory;
		private final List<Asset> assets = new ArrayList<>();

		private Folder(File directory) { this.directory = directory; }

		public File getDirectory() { return directory; }
		public String getName() { return directory.getName(); }
		public List<Asset> getAssets() {
			return Collections.unmodifiableList(assets);
		}
	}

	private final Map<Integer, Asset> assets = new LinkedHashMap<>();
	private final Map<String, Folder> folders = new LinkedHashMap<>();
	private final List<Listener> listeners = new ArrayList<>();
	private int nextId = 1;

	public void addListener(Listener listener) { listeners.add(listener); }

	public void addFolder(File folder) throws IOException {
		if (folder == null || !folder.isDirectory()) {
			throw new IOException("That path is not a folder.");
		}
		File normalizedFolder = folder.getCanonicalFile();
		String key = normalizedFolder.getPath();
		if (folders.containsKey(key)) return;
		Folder importedFolder = new Folder(normalizedFolder);
		folders.put(key, importedFolder);
		loadFolder(normalizedFolder, importedFolder);
		notifyListeners();
	}

	private void loadFolder(File folder, Folder importedFolder) throws IOException {
		File[] files = folder.listFiles();
		if (files == null) return;
		java.util.Arrays.sort(files, (left, right) -> left.getName().compareToIgnoreCase(right.getName()));
		for (File file : files) {
			if (file.isDirectory()) {
				loadFolder(file, importedFolder);
			} else if (isImage(file)) {
				BufferedImage image = ImageIO.read(file);
				if (image != null) {
					Asset asset = new Asset(nextId, file, image, importedFolder);
					assets.put(nextId, asset);
					importedFolder.assets.add(asset);
					nextId++;
				}
			}
		}
	}

	public Asset get(int id) { return assets.get(id); }

	public void clear() {
		assets.clear();
		folders.clear();
		nextId = 1;
		notifyListeners();
	}

	public List<Asset> getAssets() {
		return Collections.unmodifiableList(new ArrayList<>(assets.values()));
	}

	public List<Folder> getFolders() {
		return Collections.unmodifiableList(new ArrayList<>(folders.values()));
	}

	private boolean isImage(File file) {
		String name = file.getName().toLowerCase();
		return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
				|| name.endsWith(".gif") || name.endsWith(".bmp");
	}

	private void notifyListeners() {
		for (Listener listener : listeners) listener.assetsChanged();
	}
}

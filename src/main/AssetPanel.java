package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

public class AssetPanel extends JPanel implements AssetManager.Listener {
	public interface Listener { void assetSelected(AssetManager.Asset asset); }

	private final AssetManager assetManager;
	private final DefaultListModel<AssetManager.Asset> model = new DefaultListModel<>();
	private final JList<AssetManager.Asset> assetList = new JList<>(model);
	private final JComboBox<FolderChoice> folderChooser = new JComboBox<>();
	private Listener listener;

	public AssetPanel(AssetManager assetManager) {
		super(new BorderLayout(6, 6));
		this.assetManager = assetManager;
		assetManager.addListener(this);
		setBorder(BorderFactory.createTitledBorder("Assets"));
		JButton addFolder = new JButton("Add folder");
		addFolder.addActionListener(event -> chooseFolder());
		folderChooser.addActionListener(event -> refreshAssets());
		javax.swing.JPanel folderBar = new javax.swing.JPanel(new BorderLayout(4, 0));
		folderBar.add(folderChooser, BorderLayout.CENTER);
		folderBar.add(addFolder, BorderLayout.EAST);
		add(folderBar, BorderLayout.NORTH);
		assetList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		assetList.setVisibleRowCount(0);
		assetList.setFixedCellWidth(96);
		assetList.setFixedCellHeight(104);
		assetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		assetList.setCellRenderer(new AssetRenderer());
		assetList.addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting() && listener != null && assetList.getSelectedValue() != null) {
				listener.assetSelected(assetList.getSelectedValue());
			}
		});
		add(new JScrollPane(assetList), BorderLayout.CENTER);
	}

	public void setListener(Listener listener) { this.listener = listener; }

	private void chooseFolder() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				assetManager.addFolder(chooser.getSelectedFile());
			} catch (IOException exception) {
				javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Import failed",
						javax.swing.JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override public void assetsChanged() {
		FolderChoice previous = (FolderChoice) folderChooser.getSelectedItem();
		folderChooser.removeAllItems();
		folderChooser.addItem(FolderChoice.allFolders());
		for (AssetManager.Folder folder : assetManager.getFolders()) {
			folderChooser.addItem(new FolderChoice(folder));
		}
		if (previous != null) {
			for (int index = 0; index < folderChooser.getItemCount(); index++) {
				if (folderChooser.getItemAt(index).matches(previous)) {
					folderChooser.setSelectedIndex(index);
					break;
				}
			}
		}
		refreshAssets();
	}

	private void refreshAssets() {
		model.clear();
		FolderChoice choice = (FolderChoice) folderChooser.getSelectedItem();
		if (choice == null || choice.folder == null) {
			for (AssetManager.Asset asset : assetManager.getAssets()) model.addElement(asset);
		} else {
			for (AssetManager.Asset asset : choice.folder.getAssets()) model.addElement(asset);
		}
	}

	private static final class FolderChoice {
		private final AssetManager.Folder folder;
		private final boolean all;

		private FolderChoice(AssetManager.Folder folder) {
			this.folder = folder;
			this.all = false;
		}

		private FolderChoice() {
			this.folder = null;
			this.all = true;
		}

		private static FolderChoice allFolders() { return new FolderChoice(); }

		private boolean matches(FolderChoice other) {
			return all == other.all && (folder == null || folder.getDirectory().equals(other.folder.getDirectory()));
		}

		@Override public String toString() {
			return all ? "All folders" : folder.getName();
		}
	}

	private static final class AssetRenderer extends JPanel implements ListCellRenderer<AssetManager.Asset> {
		private final JLabel image = new JLabel();
		private final JLabel name = new JLabel();

		private AssetRenderer() {
			super(new BorderLayout(2, 2));
			setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			image.setHorizontalAlignment(JLabel.CENTER);
			name.setHorizontalAlignment(JLabel.CENTER);
			add(image, BorderLayout.CENTER);
			add(name, BorderLayout.SOUTH);
		}

		@Override public Component getListCellRendererComponent(JList<? extends AssetManager.Asset> list,
				AssetManager.Asset asset, int index, boolean selected, boolean focused) {
			BufferedImage source = asset.getImage();
			Image thumbnail = source.getScaledInstance(72, 72, Image.SCALE_SMOOTH);
			image.setIcon(new ImageIcon(thumbnail));
			name.setText(asset.getId() + ": " + asset.getName());
			setBackground(selected ? list.getSelectionBackground() : list.getBackground());
			name.setForeground(selected ? list.getSelectionForeground() : list.getForeground());
			return this;
		}
	}
}

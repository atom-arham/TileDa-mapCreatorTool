package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

public class MainPanel extends JFrame implements Runnable{
    private final TileMap map = new TileMap(16, 16);
    private final AssetManager assetManager = new AssetManager();
    private final GridPanel gridPanel = new GridPanel(map);

    public MainPanel(){
        super("TileDa 0.30");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));

        setJMenuBar(createMenuBar());
        AssetPanel assets = new AssetPanel(assetManager);
        assets.setPreferredSize(new Dimension(240, 500));
        assets.setListener(gridPanel::setSelectedAsset);
        EditorToolbar toolbar = new EditorToolbar(new EditorToolbar.Listener() {
            @Override public void toolChanged(GridPanel.Tool tool) { gridPanel.setTool(tool); }
            @Override public void brushSizeChanged(int size) { gridPanel.setBrushSize(size); }
            @Override public void gridSizeChanged(int size) { gridPanel.resizeGrid(size); }
            @Override public void zoomChanged(boolean zoomIn) {
                if (zoomIn) gridPanel.zoomIn(); else gridPanel.zoomOut();
            }
        });
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
        add(assets, BorderLayout.WEST);

        setMinimumSize(new Dimension(720,560));
        pack();
        setSize(Math.max(getWidth(), 1000), Math.max(getHeight(), 700));
        setLocationByPlatform(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem save = new JMenuItem("Save project");
        save.addActionListener(event -> saveProject());
        JMenuItem open = new JMenuItem("Open project");
        open.addActionListener(event -> openProject());
        JMenuItem exportPng = new JMenuItem("Export PNG");
        exportPng.addActionListener(event -> exportPng());
        JMenuItem exportJson = new JMenuItem("Export JSON");
        exportJson.addActionListener(event -> exportJson());
        file.add(open);
        file.add(save);
        file.addSeparator();
        file.add(exportPng);
        file.add(exportJson);
        JMenu settings = new JMenu("Settings");
        JMenuItem resetZoom = new JMenuItem("Reset zoom");
        resetZoom.addActionListener(event -> gridPanel.resetZoom());
        settings.add(resetZoom);
        menuBar.add(file);
        menuBar.add(settings);
        return menuBar;
    }

    private void saveProject() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save TileDa project");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                ProjectFile.save(file, map, assetManager);
            } catch (IOException exception) {
                javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Save failed",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openProject() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open TileDa project");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ProjectFile.load(chooser.getSelectedFile(), map, assetManager);
                gridPanel.refreshAfterLoad();
            } catch (IOException exception) {
                javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Open failed",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportPng() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export map as PNG");
        chooser.setSelectedFile(new File("tilemap.png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                MapExporter.exportPng(ensureExtension(chooser.getSelectedFile(), ".png"), map);
            } catch (IOException exception) {
                showExportError(exception);
            }
        }
    }

    private void exportJson() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export map as JSON");
        chooser.setSelectedFile(new File("tilemap.json"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                MapExporter.exportJson(ensureExtension(chooser.getSelectedFile(), ".json"), map);
            } catch (IOException exception) {
                showExportError(exception);
            }
        }
    }

    private File ensureExtension(File file, String extension) {
        if (file.getName().toLowerCase().endsWith(extension)) return file;
        return new File(file.getParentFile(), file.getName() + extension);
    }

    private void showExportError(IOException exception) {
        javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Export failed",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void run() {
        setVisible(true);
    }

}

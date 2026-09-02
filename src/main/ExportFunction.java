package main;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExportFunction {
    
    /**
     * Export grid data to a text file
     * Format: Space-separated numbers, one row per line
     */
    public static String exportGrid(Grid grid) throws IOException {
        int[][] gridData = grid.getGridData();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "map_" + timestamp + ".txt";
        
        FileWriter writer = new FileWriter(filename);
        
        // Write grid data
        for (int y = 0; y < gridData.length; y++) {
            for (int x = 0; x < gridData[y].length; x++) {
                writer.write(String.valueOf(gridData[y][x]));
                if (x < gridData[y].length - 1) {
                    writer.write(" ");
                }
            }
            writer.write("\n");
        }
        
        writer.close();
        return filename;
    }
    
    /**
     * Export grid to a specific file
     */
    public static void exportGridToFile(Grid grid, String filename) throws IOException {
        int[][] gridData = grid.getGridData();
        FileWriter writer = new FileWriter(filename);
        
        for (int y = 0; y < gridData.length; y++) {
            for (int x = 0; x < gridData[y].length; x++) {
                writer.write(String.valueOf(gridData[y][x]));
                if (x < gridData[y].length - 1) {
                    writer.write(" ");
                }
            }
            writer.write("\n");
        }
        
        writer.close();
    }
    
    /**
     * Export grid with file chooser dialog
     * Allows user to select save location and filename
     * Returns filename if successful, null if cancelled
     */
    public static String exportGridWithDialog(Grid grid, JComponent parent) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Map File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        
        // Set default filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        fileChooser.setSelectedFile(new java.io.File("map_" + timestamp + ".txt"));
        
        int result = fileChooser.showSaveDialog(parent);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String filepath = selectedFile.getAbsolutePath();
            
            // Ensure .txt extension
            if (!filepath.toLowerCase().endsWith(".txt")) {
                filepath += ".txt";
            }
            
            exportGridToFile(grid, filepath);
            return filepath;
        }
        
        return null;  // User cancelled
    }
}

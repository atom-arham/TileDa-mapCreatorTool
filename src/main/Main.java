package main;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GridCreationDialog dialog = new GridCreationDialog(null);
            Grid grid = dialog.getGrid();
            
            if (grid != null) {
                JFrame window = new JFrame();
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setResizable(false);
                window.setTitle("Map Creator");

                //main container
                JPanel container = new JPanel();
                container.setLayout(new BorderLayout());
                
                //color panel
                ColourPanel colourModel = new ColourPanel();
                
                //main panel with the color model
                MainPanel mainPanel = new MainPanel(grid, colourModel);
                
                //color side panel, keyhandler,mainpanel passwed
                ColourSidePanel colourSidePanel = new ColourSidePanel(colourModel, mainPanel.getKeyHandler(), grid);
                
                //add 
                container.add(mainPanel, BorderLayout.CENTER);
                container.add(colourSidePanel, BorderLayout.EAST);
                
                window.add(container);
                window.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        mainPanel.stopGame();
                    }
                });
                
                //window sizre
                window.setSize(1280 + ColourSidePanel.getPanelWidth(), 920);
                window.setLocationRelativeTo(null);
                window.setVisible(true);
                mainPanel.requestFocusInWindow();
            } else {
                System.exit(0);
            }
        });
    }
}

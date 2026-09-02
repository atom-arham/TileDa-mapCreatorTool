package main;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.awt.Dimension;

public class GridCreationDialog extends JDialog {
    private Grid grid;
    private boolean accepted = false;
    
    public GridCreationDialog(Frame parent) {
        super(parent, "Create Grid", true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setSize(300, 200);
        this.setLocationRelativeTo(parent);
        
        // Create panel with grid input controls
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Width input
        JPanel widthPanel = new JPanel();
        widthPanel.setLayout(new BoxLayout(widthPanel, BoxLayout.X_AXIS));
        widthPanel.setMaximumSize(new Dimension(260, 40));
        
        JLabel widthLabel = new JLabel("Grid Width (X):");
        widthLabel.setPreferredSize(new Dimension(100, 30));
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        widthSpinner.setPreferredSize(new Dimension(80, 30));
        
        widthPanel.add(widthLabel);
        widthPanel.add(Box.createHorizontalStrut(10));
        widthPanel.add(widthSpinner);
        widthPanel.add(Box.createHorizontalGlue());
        
        // Height input
        JPanel heightPanel = new JPanel();
        heightPanel.setLayout(new BoxLayout(heightPanel, BoxLayout.X_AXIS));
        heightPanel.setMaximumSize(new Dimension(260, 40));
        
        JLabel heightLabel = new JLabel("Grid Height (Y):");
        heightLabel.setPreferredSize(new Dimension(100, 30));
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        heightSpinner.setPreferredSize(new Dimension(80, 30));
        
        heightPanel.add(heightLabel);
        heightPanel.add(Box.createHorizontalStrut(10));
        heightPanel.add(heightSpinner);
        heightPanel.add(Box.createHorizontalGlue());
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setMaximumSize(new Dimension(260, 40));
        
        JButton okButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int width = (Integer) widthSpinner.getValue();
                int height = (Integer) heightSpinner.getValue();
                grid = new Grid(width, height);
                accepted = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid = null;
                accepted = false;
                dispose();
            }
        });
        
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(okButton);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(cancelButton);
        
        // Add all panels to content
        contentPanel.add(widthPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(heightPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(buttonsPanel);
        
        this.add(contentPanel);
        this.setVisible(true);
    }
    
    public Grid getGrid() {
        return grid;
    }
    
    public boolean isAccepted() {
        return accepted;
    }
}

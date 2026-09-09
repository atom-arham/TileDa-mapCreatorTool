package main;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class EditorToolbar extends JPanel {
    public interface Listener {
        void toolChanged(GridPanel.Tool tool);
        void brushSizeChanged(int size);
        void gridSizeChanged(int size);
        void zoomChanged(boolean zoomIn);
    }

    public EditorToolbar(Listener listener) {
        super(new FlowLayout(FlowLayout.LEFT, 6, 4));
        setBorder(BorderFactory.createEtchedBorder());
        JRadioButton brush = new JRadioButton("Paint brush", true);
        JRadioButton fill = new JRadioButton("Fill");
        JRadioButton rectangle = new JRadioButton("Area fill");
        ButtonGroup tools = new ButtonGroup();
        tools.add(brush); tools.add(fill); tools.add(rectangle);
        brush.addActionListener(event -> listener.toolChanged(GridPanel.Tool.PAINT_BRUSH));
        fill.addActionListener(event -> listener.toolChanged(GridPanel.Tool.FILL_CANVAS));
        rectangle.addActionListener(event -> listener.toolChanged(GridPanel.Tool.RECTANGLE_FILL));
        add(brush); add(fill); add(rectangle);

        add(new JLabel("Brush:"));
        JSpinner brushSize = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
        brushSize.addChangeListener(event -> listener.brushSizeChanged((Integer) brushSize.getValue()));
        add(brushSize);

        add(new JLabel("Grid:"));
        JComboBox<Integer> gridSize = new JComboBox<>();
        for (int size = 16; size <= 256; size *= 2) gridSize.addItem(size);
        gridSize.addActionListener(event -> listener.gridSizeChanged((Integer) gridSize.getSelectedItem()));
        add(gridSize);

        javax.swing.JButton zoomOut = new javax.swing.JButton("-");
        javax.swing.JButton zoomIn = new javax.swing.JButton("+");
        zoomOut.addActionListener(event -> listener.zoomChanged(false));
        zoomIn.addActionListener(event -> listener.zoomChanged(true));
        add(new JLabel("Zoom:")); add(zoomOut); add(zoomIn);
    }
}
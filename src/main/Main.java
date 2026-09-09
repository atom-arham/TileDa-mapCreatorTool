package main;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) throws Exception{
        SwingUtilities.invokeLater(new MainPanel());
    }
}

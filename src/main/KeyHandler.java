package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class KeyHandler implements KeyListener, MouseListener {
    
    private boolean[] keys = new boolean[256];
    private int lastNumberPressed = -1;
    private Grid grid;
    private boolean shiftPressed = false;
    private boolean ctrlPressed = false;
    private int paintNumber = -1; // Currently held number for paint mode
    private boolean lastExportPressed = false; // Track Ctrl+S for single trigger
    
    public KeyHandler(Grid grid) {
        this.grid = grid;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode < keys.length) {
            keys[keyCode] = true;
        }
        // Track shift key
        if (keyCode == KeyEvent.VK_SHIFT) {
            shiftPressed = true;
        }
        // Track ctrl key
        if (keyCode == KeyEvent.VK_CONTROL) {
            ctrlPressed = true;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode < keys.length) {
            keys[keyCode] = false;
        }
        // Track shift key release
        if (keyCode == KeyEvent.VK_SHIFT) {
            shiftPressed = false;
        }
        // Track ctrl key release
        if (keyCode == KeyEvent.VK_CONTROL) {
            ctrlPressed = false;
            paintNumber = -1; // Exit paint mode
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() / 16; // Tile size is 16
        int y = e.getY() / 16;
        grid.selectTile(x, y);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    public boolean isKeyPressed(int keyCode) {
        return keyCode < keys.length && keys[keyCode];
    }
    
    // WASD Navigation
    public boolean isUpPressed() {
        return isKeyPressed(KeyEvent.VK_W);
    }
    
    public boolean isDownPressed() {
        return isKeyPressed(KeyEvent.VK_S);
    }
    
    public boolean isLeftPressed() {
        return isKeyPressed(KeyEvent.VK_A);
    }
    
    public boolean isRightPressed() {
        return isKeyPressed(KeyEvent.VK_D);
    }

    // Arrow Navigation
    public boolean isArrowUpPressed() {
        return isKeyPressed(KeyEvent.VK_UP);
    }
    
    public boolean isArrowDownPressed() {
        return isKeyPressed(KeyEvent.VK_DOWN);
    }
    
    public boolean isArrowLeftPressed() {
        return isKeyPressed(KeyEvent.VK_LEFT);
    }
    
    public boolean isArrowRightPressed() {
        return isKeyPressed(KeyEvent.VK_RIGHT);
    }
    
    // Camera controls (Shift + Arrow keys)
    public boolean isCameraUpPressed() {
        return shiftPressed && isArrowUpPressed();
    }
    
    public boolean isCameraDownPressed() {
        return shiftPressed && isArrowDownPressed();
    }
    
    public boolean isCameraLeftPressed() {
        return shiftPressed && isArrowLeftPressed();
    }
    
    public boolean isCameraRightPressed() {
        return shiftPressed && isArrowRightPressed();
    }
    
    // Zoom controls
    public boolean isZoomInPressed() {
        return isKeyPressed(KeyEvent.VK_EQUALS) || isKeyPressed(KeyEvent.VK_PLUS);
    }
    
    public boolean isZoomOutPressed() {
        return isKeyPressed(KeyEvent.VK_MINUS) || isKeyPressed(KeyEvent.VK_UNDERSCORE);
    }
    
    // Reset view
    public boolean isResetViewPressed() {
        return shiftPressed && isKeyPressed(KeyEvent.VK_R);
    }
    
    // Paint mode - check if Ctrl is pressed and a number is being held
    public boolean isPaintModeActive() {
        return ctrlPressed && getPaintNumber() >= 0;
    }
    
    public int getPaintNumber() {
        if (ctrlPressed) {
            for (int i = 0; i <= 9; i++) {
                int numpadKey = KeyEvent.VK_NUMPAD0 + i;
                int numberKey = KeyEvent.VK_0 + i;
                if (isKeyPressed(numpadKey) || isKeyPressed(numberKey)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public boolean isCtrlPressed() {
        return ctrlPressed;
    }
    
    public void setCtrlPressed(boolean ctrl) {
        this.ctrlPressed = ctrl;
    }
    
    // Number assignment
    public int getNumberPressed() {
        for (int i = 0; i <= 9; i++) {
            int numpadKey = KeyEvent.VK_NUMPAD0 + i;
            int numberKey = KeyEvent.VK_0 + i;
            if (isKeyPressed(numpadKey) || isKeyPressed(numberKey)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getPressedNumberOnce() {
        int numPressed = getNumberPressed();
        if (numPressed != lastNumberPressed && numPressed >= 0) {
            lastNumberPressed = numPressed;
            return numPressed;
        }
        if (numPressed < 0) {
            lastNumberPressed = -1;
        }
        return -1;
    }
    
    // Export controls (Ctrl+S)
    public boolean isExportPressed() {
        boolean currentExport = ctrlPressed && isKeyPressed(KeyEvent.VK_S);
        boolean triggered = currentExport && !lastExportPressed;
        lastExportPressed = currentExport;
        return triggered;
    }
}

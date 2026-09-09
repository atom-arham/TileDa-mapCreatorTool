package main;

public class ZoomInOut {
    private static final double MIN_ZOOM = 0.25;
    private static final double MAX_ZOOM = 4;
    private static final double STEP = 1.15;
    private double zoom = 1.0;

    public double getZoom(){
        return zoom;
    }
    public void zoomIn(){
        zoom = Math.min(MAX_ZOOM, zoom * STEP);
    }
    public void zoomOut(){
        zoom = Math.max(MIN_ZOOM, zoom/STEP);
    }

    public void reset(){
        zoom = 1.0;
    }

    public int scale(int baseSize){
        return Math.max(1,(int) Math.round(baseSize * zoom));
    }
}

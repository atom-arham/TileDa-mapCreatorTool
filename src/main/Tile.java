package main;

import java.awt.image.BufferedImage;

public class Tile {
    private BufferedImage image;
    private final int assetId;
    
    public Tile(BufferedImage image){
        this(image, 0);
    }

    public Tile(BufferedImage image, int assetId){
        this.image = image;
        this.assetId = assetId;
    }
    public BufferedImage getImage(){
        return image;
    }
    public void setImage(BufferedImage image){
        this.image = image;
    }

    public int getAssetId(){
        return assetId;
    }

}

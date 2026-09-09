package main;

public class TileMap {
    private int width;
    private int height;
    private Tile[][] tiles;

    public TileMap(int width, int height){
        resize(width,height);
    }

    public void resize(int width, int height){
        if (width < 1 || height < 1){
            throw new IllegalArgumentException("Dimensions can't be negative or zero");
        } 

        Tile[][] resized = new Tile[height][width];
        if (tiles != null){
            for (int y=0; y < Math.min(height, this.height); y++){
                System.arraycopy(tiles[y], 0, resized[y], 0, Math.min(width, this.width));
            }
        }

        this.width = width;
        this.height = height;
        tiles = resized;
    }

    public int getWidth(){ return width; }
    public int getHeight(){ return height; }
    public Tile getTile(int x, int y){ return tiles[y][x]; }
    public void setTile(int x,int y, Tile tile){ tiles[y][x] = tile; }
}

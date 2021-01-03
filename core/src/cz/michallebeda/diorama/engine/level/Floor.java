package cz.michallebeda.diorama.engine.level;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing floor grid
 */
public class Floor {
    protected Tile[][] grid;
    protected boolean dirty = false;
    protected int width;
    protected int height;

    /**
     * Has to be loaded later
     */
    public Floor() {

    }

    /**
     * Blank Floor constructor
     *
     * @param region Region to fill with
     * @param width  Floor width
     * @param height floor height
     */
    public Floor(TextureRegion region, int width, int height) {
        this.width = width;
        this.height = height;

        grid = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile sprite = new Tile(x, y, region);
                sprite.setSize(1, 1);
                sprite.setPosition(x, y);
                grid[x][y] = sprite;
            }
        }

        dirty = true;
    }

    /**
     * Draws floor grid
     *
     * @param spriteBatch SpriteBatch to draw with
     * @param delta       Elapsed time from last frame in seconds
     */
    public void draw(SpriteBatch spriteBatch, float delta) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].draw(spriteBatch, delta);
            }
        }
    }

    /**
     * Sets TextureRegion for region located at given world coordinates
     *
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param region TextureRegion to be set
     * @return True if success false otherwise
     */
    public boolean setTileRegionAtWorld(float x, float y, TextureRegion region) {
        int xIndex = (int) x;
        int yIndex = (int) y;

        return setTileRegionAtIndex(xIndex, yIndex, region);
    }

    /**
     * Sets TextureRegion for region located at given local tile index
     *
     * @param xIndex X local grid index
     * @param yIndex Y local grid index
     * @param region TextureRegion to be set
     * @return True if success false otherwise
     */
    protected boolean setTileRegionAtIndex(int xIndex, int yIndex, TextureRegion region) {
        if (!isInBounds(xIndex, yIndex)) {
            return false;
        }

        Tile tile = getTileAtIndex(xIndex, yIndex);
        TextureRegion tileRegion = tile.getRegion();
        if (tileRegion != region) {
            tile.setRegion(region);

            dirty = true;
            return true;
        }

        return false;
    }

    public Tile getTileAtWorld(float x, float y) {
        return getTileAtIndex((int) x, (int) y);
    }

    public Tile getTileAtIndex(int x, int y) {
        if (isInBounds(x, y)) {
            return grid[x][y];
        }

        return null;
    }

    public boolean isInBounds(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height);
    }

    public boolean isInBounds(float x, float y) {
        return (x >= 0 && x <= width && y >= 0 && y <= height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void load(BufferedReader bufferedReader, TextureAtlas atlas) throws IOException {
        String line = bufferedReader.readLine();
        String[] parts;

        int hashMapSize = Integer.parseInt(line);
        HashMap<Integer, String> tileNameToId = new HashMap<>();
        for (int i = 0; i < hashMapSize; i++) {
            line = bufferedReader.readLine();
            parts = line.split(":");
            tileNameToId.put(Integer.parseInt(parts[1]), parts[0]);
        }

        line = bufferedReader.readLine();
        parts = line.split(" ");
        if (parts.length != 2) {
            return;
        }

        width = Integer.parseInt(parts[0]);
        height = Integer.parseInt(parts[1]);
        grid = new Tile[width][height];

        for (int y = 0; y < height; y++) {
            line = bufferedReader.readLine();
            parts = line.split(" ");
            for (int x = 0; x < width; x++) {
                int key = Integer.parseInt(parts[x]);

                Tile sprite = new Tile(x, y, atlas.findRegion(tileNameToId.get(key)));
                sprite.setSize(1, 1);
                sprite.setPosition(x, y);
                grid[x][y] = sprite;
            }
        }

        dirty = false;
    }

    public void setDirty() {
        dirty = true;
    }

    public void save(OutputStream outputStream) throws IOException {
        int i = 0;
        HashMap<String, Integer> tileNameToId = new HashMap<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String name = ((TextureAtlas.AtlasRegion) (TextureAtlas.AtlasRegion) grid[x][y].getRegion()).name;
                if (!tileNameToId.containsKey(name)) {
                    tileNameToId.put(name, i);
                    i++;
                }
            }
        }

        outputStream.write((tileNameToId.size() + "\n").getBytes());
        for (Map.Entry<String, Integer> entry : tileNameToId.entrySet()) {
            outputStream.write((entry.getKey() + ":" + entry.getValue() + "\n").getBytes());
        }

        outputStream.write((width + " " + height + "\n").getBytes());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String name = ((TextureAtlas.AtlasRegion) (TextureAtlas.AtlasRegion) grid[x][y].getRegion()).name;
                outputStream.write((tileNameToId.get(name) + (x == width - 1 ? "\n" : " ")).getBytes());
            }
        }

        dirty = false;
    }

    public Tile getTileByOffset(Tile tileToOffsetFrom, int xOffset, int yOffset) {
        int xIndex = tileToOffsetFrom.getXIndex() + xOffset;
        int yIndex = tileToOffsetFrom.getYIndex() + yOffset;

        return getTileAtIndex(xIndex, yIndex);
    }

    public void updateSurroundings() {
        for (int y = 0; y < getWidth(); y++) {
            for (int x = 0; x < getHeight(); x++) {
                Tile tile = getTileAtIndex(x, y);
                if (tile.hasAttachedObject()) {
                    tile.getAttachedGameObject().updateSurroundings(this);
                }
            }
        }
    }

    public void updateAround(Tile tile) {
        Tile neighborTile;

        neighborTile = getTileByOffset(tile, -1, 0);
        if (neighborTile != null) {
            if (neighborTile.hasAttachedObject()) {
                neighborTile.getAttachedGameObject().updateSurroundings(this);
            }
        }

        neighborTile = getTileByOffset(tile, 1, 0);
        if (neighborTile != null) {
            if (neighborTile.hasAttachedObject()) {
                neighborTile.getAttachedGameObject().updateSurroundings(this);
            }
        }

        neighborTile = getTileByOffset(tile, 0, -1);
        if (neighborTile != null) {
            if (neighborTile.hasAttachedObject()) {
                neighborTile.getAttachedGameObject().updateSurroundings(this);
            }
        }

        neighborTile = getTileByOffset(tile, 0, 1);
        if (neighborTile != null) {
            if (neighborTile.hasAttachedObject()) {
                neighborTile.getAttachedGameObject().updateSurroundings(this);
            }
        }
    }

    public void updateAround(int xIndex, int yIndex) {
        Tile tile = getTileAtIndex(xIndex, yIndex);
        if (tile != null) {
            updateAround(tile);
        }
    }
}

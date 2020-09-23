package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Floor {
    public static final int GRID_SIZE = 100;
    protected Tile[][] grid = new Tile[GRID_SIZE][GRID_SIZE];
    protected boolean dirty = false;

    public Floor(TextureRegion region) {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                Tile sprite = new Tile(x, y, region);
                sprite.setSize(1, 1);
                sprite.setPosition(x, y);
                grid[x][y] = sprite;
            }
        }
    }

    public void draw(SpriteBatch spriteBatch, float delta) {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                grid[x][y].draw(spriteBatch, delta);
            }
        }
    }

    public boolean setTileRegionAtWorld(float x, float y, TextureRegion region) {
        int xIndex = (int) x;
        int yIndex = (int) y;

        return setTileRegionAtIndex(xIndex, yIndex, region);
    }

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
        return (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE);
    }

    public boolean isInBounds(float x, float y) {
        return (x >= 0 && x <= GRID_SIZE && y >= 0 && y <= GRID_SIZE);
    }

    public int getSize() {
        return GRID_SIZE;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void load(BufferedReader bufferedReader, TextureAtlas atlas) {
        try {
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

            int width = Integer.parseInt(parts[0]);
            int height = Integer.parseInt(parts[1]);

            for (int y = 0; y < height; y++) {
                line = bufferedReader.readLine();
                parts = line.split(" ");
                for (int x = 0; x < width; x++) {
                    int key = Integer.parseInt(parts[x]);
                    grid[x][y].setRegion(atlas.findRegion(tileNameToId.get(key)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dirty = false;
    }

    public void setDirty() {
        dirty = true;
    }

    public void save(OutputStream outputStream) {
        int width = getSize();
        int height = getSize();

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

        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        dirty = false;
    }

    public Tile getTileByOffset(Tile tileToOffsetFrom, int xOffset, int yOffset) {
        int xIndex = tileToOffsetFrom.getXIndex() + xOffset;
        int yIndex = tileToOffsetFrom.getYIndex() + yOffset;

        return getTileAtIndex(xIndex, yIndex);
    }

    public void updateSurroundings() {
        for (int y = 0; y < getSize(); y++) {
            for (int x = 0; x < getSize(); x++) {
                Tile tile = getTileAtIndex(x, y);
                if (tile.hasAttachedObject()) {
                    tile.getAttachedGameObject().updateSurroundings(this);
                }
            }
        }
    }
}

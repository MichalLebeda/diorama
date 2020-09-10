package cz.shroomware.diorama.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

import cz.shroomware.diorama.editor.history.History;
import cz.shroomware.diorama.editor.history.actions.BucketTileAction;
import cz.shroomware.diorama.editor.history.actions.PlaceTileAction;

public class Ground {
    protected static final int GRID_SIZE = 100;
    protected Tile[][] grid = new Tile[GRID_SIZE][GRID_SIZE];
    protected boolean dirty = false;
    protected History history;

    public Ground(TextureRegion region, History history) {
        this.history = history;

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
                grid[x][y].draw(spriteBatch);
            }
        }
    }

    public void setTileRegionAt(float x, float y, TextureRegion region) {
        int xIndex = (int) x;
        int yIndex = (int) y;

        if (isInBounds(x, y)) {
            Tile tile = grid[xIndex][yIndex];
            TextureRegion tileRegion = tile.getRegion();
            if (tileRegion != region) {
                history.addAction(new PlaceTileAction(tile, tileRegion, region));
                tile.setRegion(region);
            }
        }
        dirty = true;
    }

    public Tile getTileAt(int x, int y) {
        if (isInBounds(x, y)) {
            return grid[x][y];
        }

        return null;
    }

    protected void floodFillTileByOffset(Tile tile,
                                         int xOffset, int yOffset,
                                         TextureRegion toReplace, TextureRegion replacement,
                                         Array<Tile> queue,
                                         BucketTileAction bucketTileAction) {

        int x = tile.getXIndex() + xOffset;
        int y = tile.getYIndex() + yOffset;

        if (isInBounds(x, y)) {
            tile = grid[x][y];
            if (tile.getRegion() == toReplace) {
                bucketTileAction.add(tile, toReplace, replacement);
                tile.setRegion(replacement);
                queue.add(tile);
            }
        }
    }

    protected void floodFillTileByOffset(Tile tile,
                                         int xOffset, int yOffset,
                                         TextureRegion toReplace, TextureRegion replacement,
                                         Array<Tile> queue) {

        int x = tile.getXIndex() + xOffset;
        int y = tile.getYIndex() + yOffset;

        if (isInBounds(x, y)) {
            tile = grid[x][y];
            if (tile.getRegion() == toReplace) {
                tile.setRegion(replacement);
                queue.add(tile);
            }
        }
    }

    protected boolean floodFill(int x, int y, TextureRegion toReplace, TextureRegion replacement) {
        Array<Tile> queue = new Array<>();

        if (toReplace == replacement) {
            return false;
        }

        Tile tile = grid[x][y];

        if (tile.getRegion() == toReplace) {
            if (history != null) {
                BucketTileAction bucketTileAction = new BucketTileAction();
                bucketTileAction.add(tile, toReplace, replacement);

                tile.setRegion(replacement);

                queue.add(tile);

                while (!queue.isEmpty()) {
                    tile = queue.get(0);
                    queue.removeIndex(0);

                    floodFillTileByOffset(tile, 1, 0, toReplace, replacement, queue, bucketTileAction);
                    floodFillTileByOffset(tile, -1, 0, toReplace, replacement, queue, bucketTileAction);
                    floodFillTileByOffset(tile, 0, 1, toReplace, replacement, queue, bucketTileAction);
                    floodFillTileByOffset(tile, 0, -1, toReplace, replacement, queue, bucketTileAction);
                }
                history.addAction(bucketTileAction);
            } else {
                tile.setRegion(replacement);

                queue.add(tile);

                while (!queue.isEmpty()) {
                    tile = queue.get(0);
                    queue.removeIndex(0);

                    floodFillTileByOffset(tile, 1, 0, toReplace, replacement, queue);
                    floodFillTileByOffset(tile, -1, 0, toReplace, replacement, queue);
                    floodFillTileByOffset(tile, 0, 1, toReplace, replacement, queue);
                    floodFillTileByOffset(tile, 0, -1, toReplace, replacement, queue);
                }
            }

            return true;
        }

        return false;
    }

    public void tileRegionBucketAt(float x, float y, TextureRegion region) {
        dirty = floodFill((int) x, (int) y, grid[(int) x][(int) y].region, region);
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

            String[] parts = line.split(" ");
            if (parts.length != 2) {
                return;
            }

            int width = Integer.parseInt(parts[0]);
            int height = Integer.parseInt(parts[1]);

            for (int y = 0; y < height; y++) {
                line = bufferedReader.readLine();
                parts = line.split(" ");
                for (int x = 0; x < width; x++) {
                    grid[x][y].setRegion(atlas.findRegion(parts[x]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dirty = false;
    }

    public void save(OutputStream outputStream) {
        int width = getSize();
        int height = getSize();

        try {
            outputStream.write((width + " " + height + "\n").getBytes());
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    outputStream.write((((TextureAtlas.AtlasRegion) grid[x][y].getRegion()).name + (x == width - 1 ? "\n" : " ")).getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dirty = false;
    }
}

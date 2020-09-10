package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.editor.history.History;
import cz.shroomware.diorama.editor.history.actions.BucketTileAction;
import cz.shroomware.diorama.editor.history.actions.PlaceTileAction;
import cz.shroomware.diorama.engine.Ground;
import cz.shroomware.diorama.engine.Tile;

public class GroundTool {
    Ground ground;
    History history;

    public GroundTool(Ground ground, History history) {
        this.ground = ground;
        this.history = history;
    }

    public void setTileRegion(float x, float y, TextureRegion region) {
        Tile tile = ground.getTileAtWorld(x, y);
        TextureRegion tileRegion = tile.getRegion();
        if (ground.setTileRegionAtWorld(x, y, region)) {
            history.addAction(new PlaceTileAction(tile, tileRegion, region));
        }
    }

    public void tileRegionBucketAt(float x, float y, TextureRegion region) {
        floodFill((int) x, (int) y, region);
    }

    private boolean floodFill(int xIndex, int yIndex, TextureRegion replacement) {
        TextureRegion toReplace;
        if (!ground.isInBounds(xIndex, yIndex)) {
            return false;
        }

        toReplace = ground.getTileAtIndex(xIndex, yIndex).getRegion();

        if (toReplace == replacement) {
            return false;
        }

        Tile tile = ground.getTileAtIndex(xIndex, yIndex);

        Array<Tile> queue = new Array<>();

        if (tile.getRegion() == toReplace) {
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

            return true;
        }

        return false;
    }

    private void floodFillTileByOffset(Tile tile,
                                       int xOffset, int yOffset,
                                       TextureRegion toReplace, TextureRegion replacement,
                                       Array<Tile> queue,
                                       BucketTileAction bucketTileAction) {

        int x = tile.getXIndex() + xOffset;
        int y = tile.getYIndex() + yOffset;

        if (ground.isInBounds(x, y)) {
            tile = ground.getTileAtIndex(x, y);
            if (tile.getRegion() == toReplace) {
                bucketTileAction.add(tile, toReplace, replacement);
                tile.setRegion(replacement);
                queue.add(tile);
            }
        }
    }
}

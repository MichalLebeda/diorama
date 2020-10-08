package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.history.actions.BucketTileAction;
import cz.shroomware.diorama.editor.history.actions.DeleteGameObjectAction;
import cz.shroomware.diorama.editor.history.actions.PlaceGameObjectAction;
import cz.shroomware.diorama.editor.history.actions.PlaceTileAction;
import cz.shroomware.diorama.engine.level.Floor;
import cz.shroomware.diorama.engine.level.Tile;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.GameObjects;

public class EditorTool {
    //    FloorTool floorTool;
//    GameObjectsTool gameObjectsTool;
    Floor floor;
    GameObjects gameObjects;
    Editor editor;

    public EditorTool(Floor floor, GameObjects gameObjects, Editor editor) {
//        floorTool = new FloorTool(floor,editor);
        this.floor = floor;
        this.gameObjects = gameObjects;
        this.editor = editor;
    }

    public void setTileRegion(float x, float y, TextureRegion region) {
        Tile tile = floor.getTileAtWorld(x, y);
        TextureRegion tileRegion = tile.getRegion();
        if (floor.setTileRegionAtWorld(x, y, region)) {
            editor.getHistory().addAction(new PlaceTileAction(tile, tileRegion, region));
            floor.setDirty();
        }
    }

    public void tileRegionBucketAt(float x, float y, TextureRegion region) {
        floodFill((int) x, (int) y, region);
    }

    private boolean floodFill(int xIndex, int yIndex, TextureRegion replacement) {
        TextureRegion toReplace;
        if (!floor.isInBounds(xIndex, yIndex)) {
            return false;
        }

        toReplace = floor.getTileAtIndex(xIndex, yIndex).getRegion();

        if (toReplace == replacement) {
            return false;
        }

        Tile tile = floor.getTileAtIndex(xIndex, yIndex);

        Array<Tile> queue = new Array<>();

        if (tile.getRegion() == toReplace) {
            BucketTileAction bucketTileAction = new BucketTileAction();
            bucketTileAction.add(tile, toReplace, replacement);

            tile.setRegion(replacement);
            floor.setDirty();

            queue.add(tile);

            while (!queue.isEmpty()) {
                tile = queue.get(0);
                queue.removeIndex(0);

                floodFillTileByOffset(tile, 1, 0, toReplace, replacement, queue, bucketTileAction);
                floodFillTileByOffset(tile, -1, 0, toReplace, replacement, queue, bucketTileAction);
                floodFillTileByOffset(tile, 0, 1, toReplace, replacement, queue, bucketTileAction);
                floodFillTileByOffset(tile, 0, -1, toReplace, replacement, queue, bucketTileAction);
            }
            editor.getHistory().addAction(bucketTileAction);

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

        if (floor.isInBounds(x, y)) {
            tile = floor.getTileAtIndex(x, y);
            if (tile.getRegion() == toReplace) {
                bucketTileAction.add(tile, toReplace, replacement);
                tile.setRegion(replacement);
                queue.add(tile);
            }
        }
    }

    public void addObject(GameObject gameObject, boolean useHistory) {
        if (gameObject.getPrototype().isAttached()) {
            Vector3 position = gameObject.getPosition();
            addObjectToTileAt(position.x, position.y, gameObject);
        } else {
            gameObjects.add(gameObject);
        }

        if (useHistory) {
            editor.getHistory().addAction(new PlaceGameObjectAction(gameObject, this));
        }
    }

    public void removeObject(GameObject gameObject, boolean useHistory) {
        Tile tile = gameObject.getTileAttachedTo();
        if (tile != null) {
            tile.detachObject();
            floor.updateSurroundings();
        }
        gameObjects.remove(gameObject);

        if (useHistory) {
            editor.getHistory().addAction(new DeleteGameObjectAction(gameObject, this));
        }
    }

    private void addObjectToTileAt(float x, float y, GameObject object) {
        Tile tile = floor.getTileAtWorld(x, y);

        if (tile != null) {
            GameObject attachedObject = tile.getAttachedGameObject();
            if (attachedObject != null) {
                gameObjects.remove(attachedObject);
            }
            tile.attachObject(object);
            object.attachToTile(tile);
            gameObjects.add(object);

            floor.updateSurroundings();
        }
    }

    public void transformObject(float x, float y, GameObject gameObject) {
        gameObject.setPosition(gameObject.getX() + x, gameObject.getY() + y);
    }

    public void transformObjectZ(float z, GameObject gameObject) {
        gameObject.setZ(gameObject.getZ() + z);
    }

    public void moveObject(float x, float y, GameObject gameObject) {
        if (gameObject.getPrototype().isAttached()) {
            x = ((int) x) + 0.5f;
            y = ((int) y) + 0.5f;

            if (floor.isInBounds(x, y)) {
                Tile tile = floor.getTileAtWorld(x, y);
                GameObject attachedObject = tile.getAttachedGameObject();
                if (attachedObject == null) {
                    gameObject.setPosition(x, y);

                    Tile lastAttachedTile = gameObject.getTileAttachedTo();
                    lastAttachedTile.detachObject();

                    gameObject.attachToTile(tile);
                    tile.attachObject(gameObject);

                    floor.updateAround(lastAttachedTile);
                    floor.updateAround(tile);
                    gameObject.updateSurroundings(floor);
                }
            }
        } else {
            if (editor.getHardSnap()) {
                //MOVE ROUND TO SEPARATE METHOD
                x = ((int) x) + 0.5f + editor.getSnapOffsetX();
                y = ((int) y) + 0.5f + editor.getSnapOffsetY();

                if (floor.isInBounds(x, y)) {
                    gameObject.setPosition(x, y);
                }
            } else {
                if (floor.isInBounds(x, y)) {
                    Vector2 position = new Vector2(x, y);
                    //TODO what about rotation?
                    position = Utils.roundPosition(position, gameObject.getWidth());
                    gameObject.setPosition(position);
                }
            }
        }
        gameObjects.setDirty();
    }
}

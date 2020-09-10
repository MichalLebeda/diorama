package cz.shroomware.diorama.engine;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.history.History;

public class Level {
    protected String filename;
    protected Ground ground;
    protected GameObjects gameObjects;

    //TODO ZBAVIT SE ATLASU JEHO ZABALENIM DO NEJAKYHO OBJEKTU S PROTOTYPAMA
    //TODO ZBAVIT SE HISTORIE AT JE TO HEZKY ROZDELENY
    public Level(String filename, History history, Array<GameObjectPrototype> gameObjectPrototypes, TextureAtlas atlas) {
        this.filename = filename;
        ground = new Ground(atlas.findRegion("floor"), history);
        gameObjects = new GameObjects(history);
        loadIfExists(gameObjectPrototypes, atlas);
    }

    public boolean loadIfExists(Array<GameObjectPrototype> gameObjectPrototypes, TextureAtlas atlas) {
        FileHandle fileHandle = Utils.getFileHandle(filename);
        if (fileHandle.exists()) {
            InputStream inputStream = fileHandle.read();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            ground.load(bufferedReader, atlas);
            gameObjects.load(bufferedReader, gameObjectPrototypes);

            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            // Save as new if level file doesn't exist
            save(true);
        }

        return false;
    }

    public boolean save(boolean force) {
        if (isDirty() || force) {
            OutputStream outputStream = Utils.getFileHandle(filename).write(false);
            ground.save(outputStream);
            gameObjects.save(outputStream);
            return true;
        }

        return false;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void draw(SpriteBatch spriteBatch, MinimalisticDecalBatch decalBatch, float delta) {
        ground.draw(spriteBatch, delta);
        gameObjects.drawShadows(spriteBatch);

        gameObjects.drawObjects(decalBatch);
    }

    public boolean isInBounds(float x, float y) {
        return ground.isInBounds(x, y);
    }

    public int getSize() {
        return ground.getSize();
    }

    public boolean isDirty() {
        return gameObjects.isDirty() || ground.isDirty();
    }

    public void addObject(GameObject object) {
        gameObjects.add(object);
    }

    public void removeObject(GameObject object) {
        gameObjects.remove(object);
    }

    public void setTileAt(float x, float y, TextureRegion region) {
        ground.setTileRegionAt(x, y, region);
    }

    public void setTileBucketAt(float x, float y, TextureRegion region) {
        ground.tileRegionBucketAt(x, y, region);
    }

    public GameObject findIntersectingWithRay(Ray ray, Vector3 cameraPos) {
        return gameObjects.findIntersectingWithRay(ray,cameraPos);
    }

    public Ground getGrid() {
        return ground;
    }
}

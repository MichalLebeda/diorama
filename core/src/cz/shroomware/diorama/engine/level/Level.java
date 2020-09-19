package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.GameObjects;

public class Level {
    protected String filename;
    protected Floor floor;
    protected cz.shroomware.diorama.engine.level.object.GameObjects gameObjects;

    //TODO ZBAVIT SE ATLASU JEHO ZABALENIM DO NEJAKYHO OBJEKTU S PROTOTYPAMA
    //TODO ZBAVIT SE HISTORIE AT JE TO HEZKY ROZDELENY
    public Level(String filename, Prototypes gameObjectPrototypes, Resources resources) {
        this.filename = filename;
        floor = new Floor(resources.getObjectAtlas().findRegion("floor"));
        gameObjects = new GameObjects();
        loadIfExists(gameObjectPrototypes, resources.getObjectAtlas());
    }

    public boolean loadIfExists(Prototypes gameObjectPrototypes, TextureAtlas atlas) {
        FileHandle fileHandle = Utils.getFileHandle(filename);
        if (fileHandle.exists()) {
            InputStream inputStream = fileHandle.read();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            floor.load(bufferedReader, atlas);
            gameObjects.load(bufferedReader, gameObjectPrototypes, floor);

            floor.updateSurroundings();

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
            floor.save(outputStream);
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
        floor.draw(spriteBatch, delta);

        spriteBatch.flush();
        //TODO: pouzit decaly na vsechno aby se tomuhle zamezilo (stiny na podlaze prekryv atd)
//        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(false);
        gameObjects.drawShadows(spriteBatch);

        gameObjects.drawObjects(decalBatch, delta);
    }

    public boolean isInBounds(float x, float y) {
        return floor.isInBounds(x, y);
    }

    public int getSize() {
        return floor.getSize();
    }

    public boolean isDirty() {
        return gameObjects.isDirty() || floor.isDirty();
    }

//    public void addObject(GameObject object) {
//        gameObjects.add(object);
//    }
//
//    public void removeObject(GameObject object) {
//        gameObjects.remove(object);
//    }

    public GameObject findIntersectingWithRay(Ray ray, Vector3 cameraPos) {
        return gameObjects.findIntersectingWithRay(ray, cameraPos);
    }

    public Floor getFloor() {
        return floor;
    }

    public GameObjects getGameObjects() {
        return gameObjects;
    }
}
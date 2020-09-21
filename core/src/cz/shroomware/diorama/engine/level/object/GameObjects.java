package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

import cz.shroomware.diorama.engine.level.Floor;
import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.engine.level.Tile;
import cz.shroomware.diorama.engine.level.prototype.Prototype;

public class GameObjects {
    protected Array<GameObject> gameObjects = new Array<>();
    protected boolean dirty = false;

    public void drawShadows(Batch batch) {
        for (GameObject object : gameObjects) {
            object.drawShadow(batch);
        }
    }

    public void drawObjects(MinimalisticDecalBatch decalBatch, float delta) {
        for (GameObject object : gameObjects) {
            object.drawDecal(decalBatch, delta);
        }
    }

    public void add(GameObject gameObject) {
        dirty = true;
        gameObjects.add(gameObject);
    }

    public void remove(GameObject gameObject) {
        dirty = true;
        gameObjects.removeValue(gameObject, false);
    }

    public GameObject findIntersectingWithRay(Ray ray, Vector3 cameraPos) {
        Vector3 intersection = new Vector3();
        BoundingBox boundingBox = new BoundingBox();


        float minDist = Float.MAX_VALUE;
        GameObject candidate = null;
        //test ray against every game object we have
        for (GameObject gameObject : gameObjects) {
            gameObject.sizeBoundingBox(boundingBox);
            //TODO: IF DECAL WAS ROTATED BY NON MULTIPLE OF 90, PASSED POSITION WILL FAIL COS BOUNDS WILL BE NON PLANAR
            if (Intersector.intersectRayBounds(ray, boundingBox, intersection)) {
                if (gameObject.isPixelOpaque(intersection.cpy())) {
                    float currentObjectDist = cameraPos.cpy().add(intersection.cpy().scl(-1)).len();
                    if (currentObjectDist < minDist) {
                        minDist = currentObjectDist;
                        candidate = gameObject;
                    }
                }
            }
        }

        return candidate;
    }

    public void save(OutputStream outputStream) {
        Gdx.app.log("GameObject", "saved");
        try {
            for (GameObject object : gameObjects) {
                object.save(outputStream);
            }

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void load(BufferedReader bufferedReader, Prototypes gameObjectPrototypes, Floor floor) {
        gameObjects.clear();

        String line = null;
        try {

            Vector3 position = new Vector3();
            while ((line = bufferedReader.readLine()) != null) {
//                Gdx.app.error("LINE", line);

                String[] attributes = line.split(" ");

                if (attributes.length != 8) {
                    continue;
                }
                position.set(
                        Float.parseFloat(attributes[1]),
                        Float.parseFloat(attributes[2]),
                        Float.parseFloat(attributes[3]));

                for (int i = 0; i < gameObjectPrototypes.getSize(); i++) {
                    Prototype prototype = gameObjectPrototypes.getGameObjectPrototype(i);
                    if (attributes[0].equals(prototype.getName())) {
//                        Gdx.app.error("LINE 2", prototype.getName());
                        Quaternion quaternion = new Quaternion(
                                Float.parseFloat(attributes[4]),
                                Float.parseFloat(attributes[5]),
                                Float.parseFloat(attributes[6]),
                                Float.parseFloat(attributes[7]));
                        GameObject object = prototype.createAt(
                                //TODO add Z
                                position.x, position.y,
                                quaternion);
//                        GameObject object = prototype.createAt(position, quaternion);
                        object.setRotation(quaternion);
                        if (prototype.isAttached()) {
                            // There should always be a Tile for an attached object. Any exceptions are not our fault.
                            Tile tile = floor.getTileAtWorld(position.x, position.y);
                            tile.attachObject(object);
                            object.attachToTile(tile);
                        }
                        add(object);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dirty = false;
//
//        int i = 0;
//        for (GameObject object : gameObjects) {
//            Gdx.app.error("OBJ " + i++, object.toString());
//        }
    }
}

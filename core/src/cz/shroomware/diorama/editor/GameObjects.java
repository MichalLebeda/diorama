package cz.shroomware.diorama.editor;

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

public class GameObjects {
    protected Array<GameObject> gameObjects = new Array<>();
    protected boolean dirty = false;
    protected History history;

    public GameObjects(History history) {
        this.history = history;
    }

    public void drawShadows(Batch batch) {
        for (GameObject object : gameObjects) {
            object.drawShadow(batch);
        }
    }

    public void drawObjects(MinimalisticDecalBatch decalBatch) {
        for (GameObject object : gameObjects) {
            object.drawDecal(decalBatch);
        }
    }

    public void addNoHistory(GameObject gameObject) {
        dirty = true;
        gameObjects.add(gameObject);
    }

    public void add(GameObject gameObject) {
        dirty = true;
        gameObjects.add(gameObject);
        history.addAction(new PlaceGameObjectAction(gameObject, this));
    }

    public void removeNoHistory(GameObject gameObject) {
        dirty = true;
        gameObjects.removeValue(gameObject, false);
    }

    public void remove(GameObject gameObject) {
        dirty = true;
        gameObjects.removeValue(gameObject, false);
        history.addAction(new DeleteGameObjectAction(gameObject, this));
    }

    public GameObject findIntersectingWithRay(Ray ray) {
        Vector3 intersection = new Vector3();
        BoundingBox boundingBox = new BoundingBox();

        //test ray against every game object we have, TODO: improve
        for (GameObject gameObject : gameObjects) {
            gameObject.sizeBoundingBox(boundingBox);
            if (Intersector.intersectRayBounds(ray, boundingBox, intersection)) {
                return gameObject;
            }
        }

        return null;
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

    public void load(BufferedReader bufferedReader, Array<GameObjectPrototype> prototypes) {
        gameObjects.clear();

        String line = null;
        try {

            Vector3 position = new Vector3();
            while ((line = bufferedReader.readLine()) != null) {
                Gdx.app.error("LINE", line);

                String[] attributes = line.split(" ");

                if (attributes.length != 8) {
                    continue;
                }
//                position.set(
//                        Float.parseFloat(attributes[1]),
//                        Float.parseFloat(attributes[2]),
//                        Float.parseFloat(attributes[3]));

                for (GameObjectPrototype prototype : prototypes) {
                    if (attributes[0].equals(prototype.getName())) {
                        Gdx.app.error("LINE 2", prototype.getName());
                        Quaternion quaternion = new Quaternion(
                                Float.parseFloat(attributes[4]),
                                Float.parseFloat(attributes[5]),
                                Float.parseFloat(attributes[6]),
                                Float.parseFloat(attributes[7]));
                        GameObject object = prototype.createAt(
                                Float.parseFloat(attributes[1]),
                                Float.parseFloat(attributes[2]),
                                quaternion);
//                        GameObject object = prototype.createAt(position, quaternion);
                        object.setRotation(quaternion);
                        addNoHistory(object);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dirty = false;

        int i = 0;
        for (GameObject object : gameObjects) {
            Gdx.app.error("OBJ " + i++, object.toString());
        }
    }
}

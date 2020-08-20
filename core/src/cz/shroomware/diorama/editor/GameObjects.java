package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;
import java.io.OutputStream;

public class GameObjects {
    protected Editor editor;
    protected Array<GameObject> gameObjects = new Array<>();

    public GameObjects(Editor editor) {
        this.editor = editor;
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
        gameObjects.add(gameObject);
    }

    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
        editor.getHistory().addAction(new PlaceGameObjectAction(gameObject, this));
    }

    public void removeNoHistory(GameObject gameObject) {
        gameObjects.removeValue(gameObject, false);
    }

    public void remove(GameObject gameObject) {
        gameObjects.removeValue(gameObject, false);
        editor.getHistory().addAction(new DeleteGameObjectAction(gameObject, this));
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

    public void save() {
        OutputStream outputStream = editor.getSaveFile().write(false);
        try {
            for (GameObject object : gameObjects) {
                object.save(outputStream);
            }

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(Array<GameObjectPrototype> prototypes) {
        gameObjects.clear();

        String objectData = editor.getSaveFile().readString();
        String[] objectLines = objectData.split("\n");

        Vector3 position = new Vector3();
        for (String objectLine : objectLines) {
            String[] atributes = objectLine.split(" ");

            position.set(
                    Float.parseFloat(atributes[1]),
                    Float.parseFloat(atributes[2]),
                    Float.parseFloat(atributes[3]));

            for (GameObjectPrototype prototype : prototypes) {
                if (atributes[0].equals(prototype.getName())) {
                    addNoHistory(prototype.createAt(position));
                }
            }
        }
    }
}

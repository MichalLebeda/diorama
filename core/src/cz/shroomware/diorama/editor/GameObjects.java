package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class GameObjects {
    protected History history;
    protected Array<GameObject> gameObjects = new Array<>();

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

    public void addNoHistory(GameObject gameObject){
        gameObjects.add(gameObject);
    }

    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
        history.addAction(new PlaceGameObjectAction(gameObject, this));
    }

    public void removeNoHistory(GameObject gameObject) {
        gameObjects.removeValue(gameObject, false);
    }

    public void remove(GameObject gameObject) {
        gameObjects.removeValue(gameObject, false);
        history.addAction(new DeleteGameObjectAction(gameObject,this));
    }

    public GameObject findIntersectingWithRay(Ray ray){
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

    public void save(){

    }
}

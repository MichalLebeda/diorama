package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import cz.shroomware.diorama.engine.level.Floor;
import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.engine.level.Tile;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.prototype.Prototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class GameObjects {
    protected Array<GameObject> gameObjects = new Array<>();
    protected boolean dirty = false;
    protected Logic logic;

    public GameObjects(Logic logic) {
        this.logic = logic;
    }

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
        logic.register(gameObject);
    }

    public void remove(GameObject gameObject) {
        dirty = true;
        gameObjects.removeValue(gameObject, false);
        if (gameObject.hasBody()) {
            Body body = gameObject.getBody();
            World world = body.getWorld();
            world.destroyBody(body);
        }

        logic.unregister(gameObject);
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

    HashMap<String, GameObject> idToObject = new HashMap<>();

    public boolean isDirty() {
        return dirty;
    }

    public void save(OutputStream outputStream) {
        Gdx.app.log("GameObject", "saved");
        try {
            for (GameObject object : gameObjects) {
                outputStream.write((object.toString() + "\n").getBytes());
            }

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dirty = false;
    }

    public void load(BufferedReader bufferedReader,
                     Prototypes gameObjectPrototypes,
                     Floor floor,
                     BoxFactory boxFactory,
                     Logic logic) {
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

                    String[] parts = attributes[0].split(":");
                    String name = parts[0];
                    String id = null;
                    if (parts.length > 1) {
                        id = parts[1];
                    }

                    if (name.equals(prototype.getName())) {
//                        Gdx.app.error("LINE 2", prototype.getName());
                        Quaternion quaternion = new Quaternion(
                                Float.parseFloat(attributes[4]),
                                Float.parseFloat(attributes[5]),
                                Float.parseFloat(attributes[6]),
                                Float.parseFloat(attributes[7]));
                        GameObject object = prototype.createAt(
                                //TODO add Z
                                position.x, position.y,
                                quaternion, boxFactory);
//                        GameObject object = prototype.createAt(position, quaternion);
//                        object.setRotation(quaternion);
                        if (prototype.isAttached()) {
                            // There should always be a Tile for an attached object. Any exceptions are not our fault.
                            Tile tile = floor.getTileAtWorld(position.x, position.y);
                            tile.attachObject(object);
                            object.attachToTile(tile);
                        }
                        if (id != null) {
                            assignId(object, id);
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
    }

    public boolean assignId(GameObject object, String id) {
        id = id.replace(" ", "_");
        id = id.replace(":", "_");

        if (idToObject.containsKey(id)) {
            Gdx.app.error("GameObjects", "Duplicate ID" + id);
            return false;
        } else if (idToObject.containsKey(object.getId())) {
            dirty = true;
            idToObject.remove(object.getId());
            idToObject.put(id, object);
            object.setId(id);
            return true;
        } else {
            dirty = true;
            idToObject.put(id, object);
            object.setId(id);
            return true;
        }
    }
}

package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import cz.shroomware.diorama.engine.IdGenerator;
import cz.shroomware.diorama.engine.Identifier;
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
    HashMap<String, GameObject> idToObject = new HashMap<>();

    public GameObjects(Logic logic) {
        this.logic = logic;
    }

    public void update(float delta) {
        for (GameObject object : gameObjects) {
            object.update(delta);
        }
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

    public void add(GameObject gameObject) {
        dirty = true;
        gameObjects.add(gameObject);

        if (gameObject.hasLogicComponent()) {
            // Register object to logic system
            logic.register(gameObject.getLogicComponent());
        }
    }

    public void remove(GameObject gameObject) {
        dirty = true;
        gameObjects.removeValue(gameObject, false);
        idToObject.remove(gameObject.getIdentifier().getId());
        if (gameObject.hasBody()) {
            Body body = gameObject.getBody();
            World world = body.getWorld();
            world.destroyBody(body);
        }

        if (gameObject.hasLogicComponent()) {
            // Unregister object from the logic system
            logic.unregister(gameObject.getLogicComponent());
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void save(OutputStream outputStream) throws IOException {
        Gdx.app.log("GameObjects", "Saved");
        outputStream.write((gameObjects.size + "\n").getBytes());
        for (GameObject object : gameObjects) {
            outputStream.write((object.toString() + "\n").getBytes());
        }

        dirty = false;
    }

    public void load(BufferedReader bufferedReader,
                     Prototypes gameObjectPrototypes,
                     Floor floor,
                     BoxFactory boxFactory,
                     IdGenerator idGenerator) throws IOException {
        gameObjects.clear();

        String line;
        Vector3 position = new Vector3();
        int objectAmount = Integer.parseInt(bufferedReader.readLine());
        for (int j = 0; j < objectAmount; j++) {

            line = bufferedReader.readLine();
            String[] attributes = line.split(" ");

            String prototypeName = attributes[0];

            position.set(
                    Float.parseFloat(attributes[1]),
                    Float.parseFloat(attributes[2]),
                    Float.parseFloat(attributes[3]));

            Quaternion quaternion = new Quaternion(
                    Float.parseFloat(attributes[4]),
                    Float.parseFloat(attributes[5]),
                    Float.parseFloat(attributes[6]),
                    Float.parseFloat(attributes[7]));

            String id = attributes[8];
            Identifier identifier = idGenerator.obtainLoadedIdentifier(id);

            if (attributes.length == 10) {
                String name = attributes[9];
                identifier.setName(name);
            }

            for (int i = 0; i < gameObjectPrototypes.getSize(); i++) {
                Prototype prototype = gameObjectPrototypes.getGameObjectPrototype(i);

                if (prototypeName.equals(prototype.getName())) {
                    GameObject object = prototype.createAt(
                            position.cpy(),
                            boxFactory,
                            identifier);
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

        dirty = false;
    }

//    @Override
//    public boolean assignId(GameObject object, String id) {
//        return assignId(object, id, null);
//    }
//
//    @Override
//    public boolean assignId(GameObject object, String id, Messages messages) {
//        if (id == null || id.equals("")) {
//            Gdx.app.error("GameObjects", "ID NOT(!!!) changed:" + id);
//            Gdx.app.error("GameObjects", "Reason: bad ID");
//            if (messages != null) {
//                messages.showMessage("Bad ID");
//            }
//            return false;
//        }
//
//        id = id.replace(" ", "_");
//        id = id.replace(":", "_");
//
//        if (idToObject.containsKey(id)) {
//            Gdx.app.error("GameObjects", "ID NOT(!!!) changed: " + id);
//            Gdx.app.error("GameObjects", "Reason: Duplicate ID: " + id);
//            if (messages != null) {
//                messages.showMessage("Duplicate ID, using old");
//            }
//            return false;
//        } else if (idToObject.containsKey(object.getIdentifier().getIdString())) {
//            dirty = true;
//            idToObject.remove(object.getIdentifier().getIdString());
//            idToObject.put(id, object);
//            String oldId = object.getIdentifier().getIdString();
//            object.getIdentifier().setName(id);
//            logic.componentIdChange(object.getLogicComponent(), oldId);
//            if (messages != null) {
//                messages.showMessage("ID Changed");
//            }
//            return true;
//        } else {
//            dirty = true;
//            idToObject.put(id, object);
//            String oldId = object.getIdentifier().getIdString();
//            object.getIdentifier().setName(id);
//            logic.componentIdChange(object.getLogicComponent(), oldId);
//            if (messages != null) {
//                messages.showMessage("New ID assigned");
//            }
//            return true;
//        }
//    }

    public int getSize() {
        return gameObjects.size;
    }

    public GameObject get(int i) {
        return gameObjects.get(i);
    }

    public void setDirty() {
        this.dirty = true;
    }
}

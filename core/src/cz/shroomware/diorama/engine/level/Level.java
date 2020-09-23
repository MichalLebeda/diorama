package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.google.common.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.event.EventListener;
import cz.shroomware.diorama.engine.level.event.WallHitEvent;
import cz.shroomware.diorama.engine.level.fx.Clouds;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.GameObjects;
import cz.shroomware.diorama.engine.level.object.Trigger;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Level {
    protected String filename;
    protected Floor floor;
    protected GameObjects gameObjects;
    protected Clouds clouds;
    protected World world;
    protected BoxFactory boxFactory;
    protected EventBus eventBus;

    //TODO ZBAVIT SE ATLASU JEHO ZABALENIM DO NEJAKYHO OBJEKTU S PROTOTYPAMA
    //TODO ZBAVIT SE HISTORIE AT JE TO HEZKY ROZDELENY
    public Level(String filename, Prototypes gameObjectPrototypes, Resources resources) {
        this.filename = filename;

        world = new World(Vector2.Zero, true);
        world.setContactListener(new ContactListener() {

            private boolean isInContact(Contact contact, Body body) {
                return contact.getFixtureA().getBody() == body || contact.getFixtureB().getBody() == body;
            }

            private <T> boolean isInContact(Contact contact, Class<T> tClass) {
                return tClass.isInstance(contact.getFixtureA().getBody().getUserData()) ||
                        tClass.isInstance(contact.getFixtureB().getBody().getUserData());
            }

            private <T> T getFromContact(Contact contact, Class<T> tClass) {
                Fixture[] fixtures = {contact.getFixtureA(), contact.getFixtureB()};

                Object fixtureObject;
                for (Fixture fixture : fixtures) {
                    fixtureObject = fixture.getBody().getUserData();

                    if (tClass.isInstance(fixtureObject)) {
                        return (T) fixtureObject;
                    }
                }

                return null;
            }

            @Override
            public void beginContact(Contact contact) {
                if (isInContact(contact, Trigger.class)) {
                    Trigger trigger = getFromContact(contact, Trigger.class);
                    trigger.addContact();
                }
            }

            @Override
            public void endContact(Contact contact) {
                if (isInContact(contact, Trigger.class)) {
                    Trigger trigger = getFromContact(contact, Trigger.class);
                    trigger.removeContact();
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        boxFactory = new BoxFactory(world);
        boxFactory.addBox(0, 0, 1, 1);

        eventBus = new EventBus();
        EventListener listener = new EventListener();
        eventBus.register(listener);
        eventBus.post(new WallHitEvent());

        floor = new Floor(resources.getObjectAtlas().findRegion("floor"));
        gameObjects = new GameObjects();
        clouds = new Clouds(floor, resources);
        loadIfExists(gameObjectPrototypes, resources.getObjectAtlas());
    }

    public boolean loadIfExists(Prototypes gameObjectPrototypes, TextureAtlas atlas) {
        FileHandle fileHandle = Utils.getFileHandle(filename);
        if (fileHandle.exists()) {
            InputStream inputStream = fileHandle.read();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            floor.load(bufferedReader, atlas);
            gameObjects.load(bufferedReader, gameObjectPrototypes, floor, boxFactory);

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

    public void step(float delta) {
        world.step(delta, 6, 2);
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

        clouds.draw(decalBatch, delta);
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

    public World getWorld() {
        return world;
    }

    public BoxFactory getBoxFactory() {
        return boxFactory;
    }
}

package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.EngineGame;
import cz.shroomware.diorama.engine.level.fx.Clouds;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.logic.component.InitComponent;
import cz.shroomware.diorama.engine.level.logic.prototype.AndGatePrototype;
import cz.shroomware.diorama.engine.level.logic.prototype.OrGatePrototype;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.GameObjects;
import cz.shroomware.diorama.engine.level.object.Trigger;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Level {
    protected FileHandle fileHandle;
    protected Floor floor;
    protected GameObjects gameObjects;
    protected Clouds clouds;
    protected World world;
    protected Logic logic;
    protected BoxFactory boxFactory;
    protected PerspectiveCamera camera;
    protected InitComponent initComponent;

    private Level() {
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
                Body[] bodies = {contact.getFixtureA().getBody(), contact.getFixtureB().getBody()};

                Object attachedObject;
                for (Body body : bodies) {
                    attachedObject = body.getUserData();

                    if (tClass.isInstance(attachedObject)) {
                        return (T) attachedObject;
                    }
                }

                return null;
            }

            private Object getSecondFromContact(Contact contact, Object first) {
                Body[] bodies = {contact.getFixtureB().getBody(), contact.getFixtureB().getBody()};

                Object attachedObject;
                for (Body body : bodies) {
                    attachedObject = body.getUserData();

                    if (attachedObject != null && attachedObject != first) {
                        return attachedObject;
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
        logic = new Logic();
        logic.addPureLogicComponentPrototype(new OrGatePrototype());
        logic.addPureLogicComponentPrototype(new AndGatePrototype());
        initComponent = new InitComponent();
        logic.register(initComponent);
    }

    public Level(FileHandle fileHandle, EngineGame game, int width, int height) {
        this();
        this.fileHandle = fileHandle;

        floor = new Floor(game.getResources().getObjectAtlas().findRegion("floor"), width, height);
        gameObjects = new GameObjects(logic);
        clouds = new Clouds(floor, game.getResources().getObjectAtlas());

        initCamera();
    }

    public Level(FileHandle fileHandle, EngineGame game) {
        this();
        this.fileHandle = fileHandle;
        floor = new Floor();
        gameObjects = new GameObjects(logic);

        logic.register(game.getLevelSwitcher());
        load(game.getGameObjectPrototypes(), game.getResources().getObjectAtlas());

        initCamera();
    }

    public boolean load(Prototypes gameObjectPrototypes, TextureAtlas atlas) {
        if (fileHandle.exists()) {
            InputStream inputStream = fileHandle.read();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            try {
                floor.load(bufferedReader, atlas);
                gameObjects.load(bufferedReader, gameObjectPrototypes, floor, boxFactory);

                logic.load(bufferedReader);
            } catch (IOException e) {
                e.printStackTrace();
            }

            floor.updateSurroundings();

            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Gdx.app.error("Level", "Level doesn't exist");
            Gdx.app.exit();
            return false;
        }

        clouds = new Clouds(floor, atlas);

        logic.sendEvent(initComponent.getEvents().first());

        return true;
    }

    protected void initCamera() {
        camera = new PerspectiveCamera(
                50,
                Utils.calculateCameraViewportWidth(),
                Utils.calculateCameraViewportHeight());
        camera.near = 0.1f;
        camera.far = 300;

        camera.position.set(getWidth() / 2.f, -2, 5);
        camera.lookAt(getWidth() / 2.f, 4, 0);
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public boolean save(boolean force) {
        if (isDirty() || force) {
            OutputStream outputStream = fileHandle.write(false);
            try {
                floor.save(outputStream);
                gameObjects.save(outputStream);
                logic.save(outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        return false;
    }

    //    public String getFilename() {
//        return fileHandle.name();
//    }
//
    public FileHandle getFileHandle() {
        return fileHandle;
    }

    public void update(float delta) {
        world.step(delta, 6, 2);

        gameObjects.update(delta);
//        clouds.update(delta);
    }

    public void draw(SpriteBatch spriteBatch, MinimalisticDecalBatch decalBatch, float delta) {
        floor.draw(spriteBatch, delta);

        spriteBatch.flush();
        //TODO: pouzit decaly na vsechno aby se tomuhle zamezilo (stiny na podlaze prekryv atd)
//        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(false);

//        gameObjects.drawShadows(spriteBatch);
        gameObjects.drawObjects(decalBatch);
//        clouds.draw(decalBatch);
    }

    public boolean isInBounds(float x, float y) {
        return floor.isInBounds(x, y);
    }

    public int getWidth() {
        return floor.getWidth();
    }

    public int getHeight() {
        return floor.getHeight();
    }

    public boolean isDirty() {
        return gameObjects.isDirty() || floor.isDirty() || logic.isDirty();
    }

    public GameObject findIntersectingWithRay(Ray ray, Camera camera) {
        return gameObjects.findIntersectingWithRay(ray, camera);
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

    public Logic getLogic() {
        return logic;
    }

    //TODO remove
    public void dumpLogic() {
        Gdx.app.log("Level:Logic", "\n" + logic.toString());
    }

    public String getName() {
        return fileHandle.name();
    }
}

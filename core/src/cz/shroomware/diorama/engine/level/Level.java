package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.World;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.ColorUtil;
import cz.shroomware.diorama.engine.EngineGame;
import cz.shroomware.diorama.engine.IdGenerator;
import cz.shroomware.diorama.engine.level.fx.Clouds;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.logic.component.InitComponent;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.GameObjects;
import cz.shroomware.diorama.engine.level.portal.MetaPortal;
import cz.shroomware.diorama.engine.level.portal.Portal;
import cz.shroomware.diorama.engine.level.portal.Portals;
import cz.shroomware.diorama.engine.physics.BoxFactory;
import cz.shroomware.diorama.engine.physics.LevelContactListener;

public class Level {
    protected Portals portals;
    protected MetaLevel metaLevel;
    protected Floor floor;
    protected GameObjects gameObjects;
    protected Clouds clouds;
    protected World world;
    protected Logic logic;
    protected BoxFactory boxFactory;
    protected PerspectiveCamera camera;
    protected InitComponent initComponent;
    protected Resources resources;

    private Level(EngineGame game, MetaLevel metaLevel) {
        this.metaLevel = metaLevel;
        this.resources = game.getResources();

        world = new World(Vector2.Zero, true);
        world.setContactListener(new LevelContactListener());
        boxFactory = new BoxFactory(world);

        logic = new Logic(metaLevel.getParentProject().getIdGenerator());

        initComponent = new InitComponent();
        logic.register(initComponent);

        gameObjects = new GameObjects(logic);
        portals = new Portals(metaLevel, logic,
                boxFactory,
                resources);
    }

    public Level(MetaLevel metaLevel, EngineGame game, int width, int height) {
        this(game, metaLevel);

        floor = new Floor(game.getResources().getObjectAtlas().findRegion("ground"), width, height);
        clouds = new Clouds(floor, game.getResources().getObjectAtlas());

        initCamera();
    }

    public Level(MetaLevel metaLevel, EngineGame game) {
        this(game, metaLevel);

        floor = new Floor();
        load(game.getGameObjectPrototypes(), game.getResources().getObjectAtlas());
        clouds = new Clouds(floor, game.getResources().getObjectAtlas());

        initCamera();
    }

    public void setIgnoredPortal(MetaPortal metaPortal) {
        portals.setIgnored(metaPortal);
    }

    public boolean load(Prototypes gameObjectPrototypes, TextureAtlas atlas) {
        FileHandle fileHandle = metaLevel.getDataFileHandle();
        if (fileHandle.exists()) {
            InputStream inputStream = fileHandle.read();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            try {
                IdGenerator idGenerator = metaLevel.getParentProject().getIdGenerator();
                floor.load(bufferedReader, atlas);
                gameObjects.load(bufferedReader,
                        gameObjectPrototypes,
                        floor,
                        boxFactory,
                        idGenerator);

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
            Gdx.app.error("Level", fileHandle.name());
            Gdx.app.exit();
            return false;
        }

        logic.sendEvent(initComponent.getEvents().first());

        return true;
    }

    protected void initCamera() {
        camera = new PerspectiveCamera(
                45,
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
            if (gameObjects.isDirty()
                    || floor.isDirty()
                    || logic.isDirty()) {
                OutputStream outputStream = metaLevel.getDataFileHandle().write(false);
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
            }

            if (metaLevel.getMetaPortals().isDirty()) {
                OutputStream outputStream = metaLevel.getMetadataFileHandle().write(false);
                try {
                    metaLevel.getMetaPortals().save(outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

            }

            return true;
        }

        return false;
    }

    //    public String getFilename() {
//        return fileHandle.name();
//    }
//

    public void updatePhysics(float delta) {
        world.step(delta, 6, 2);
    }

    public void update(float delta) {
        gameObjects.update(delta);
        clouds.update(delta);
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

        clouds.draw(camera, decalBatch);
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
        return gameObjects.isDirty() || floor.isDirty() || logic.isDirty() || getMetaLevel().getMetaPortals().isDirty();
    }

    public GameObject findIntersectingWithRay(ColorUtil colorUtil, Ray ray, Camera camera) {
        Vector3 intersection = new Vector3();
        BoundingBox boundingBox = new BoundingBox();

        float minDist = Float.MAX_VALUE;
        GameObject candidate = null;

        // Test ray against every game object we have
        for (int i = 0; i < gameObjects.getSize(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.sizeBoundingBox(boundingBox);
            if (Intersector.intersectRayBounds(ray, boundingBox, intersection)) {
                if (gameObject.intersectsWithOpaque(colorUtil, ray, intersection.cpy())) {
                    float currentObjectDist = camera.position.cpy().add(intersection.cpy().scl(-1)).len();
                    if (currentObjectDist < minDist) {
                        minDist = currentObjectDist;
                        candidate = gameObject;
                    }
                }
            }
        }

        // Test ray against every game object we have
        Collection<Portal> portalsCollection = portals.getPortals();
        for (Portal portal : portalsCollection) {
            portal.sizeBoundingBox(boundingBox);
            if (Intersector.intersectRayBounds(ray, boundingBox, intersection)) {
                if (portal.intersectsWithOpaque(colorUtil, ray, intersection.cpy())) {
                    float currentObjectDist = camera.position.cpy().add(intersection.cpy().scl(-1)).len();
                    if (currentObjectDist < minDist) {
                        minDist = currentObjectDist;
                        candidate = portal;
                    }
                }
            }
        }

        return candidate;
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

    public MetaLevel getMetaLevel() {
        return metaLevel;
    }

    public Portals getPortals() {
        return portals;
    }
}

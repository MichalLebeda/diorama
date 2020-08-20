package cz.shroomware.diorama.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Date;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.editor.Cursor;
import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.GameObject;
import cz.shroomware.diorama.editor.GameObjectPrototype;
import cz.shroomware.diorama.editor.GameObjects;
import cz.shroomware.diorama.editor.History;
import cz.shroomware.diorama.ui.Hud;

public class DemoScreen extends BaseGameScreen {
    private static final float SCROLL_RATIO = 0.4f;
    private static final int GRID_SIZE = 200;
    private static final float DEGREES_PER_PIXEL = 0.2f;
    private static final float TRANSLATION_LIMIT = 3;
    public static final float PAN_PER_PIXEL = 0.02f;

    MinimalisticDecalBatch decalBatch;
    SpriteBatch spriteBatch;
    PerspectiveCamera camera;
    TextureAtlas atlas;
    TextureAtlas shadowsAtlas;
    TextureRegion defaultCursorRegion;
    TextureRegion floorRegion;
    TextureRegion shadowRegion;
    Vector2 lastDragScreenPos = new Vector2();
    Array<GameObjectPrototype> gameObjectPrototypes = new Array<>();
    Array<Sprite> floorSprites = new Array<>();
    GameObjects gameObjects;
    Cursor cursor;
    Hud hud;
    InputMultiplexer inputMultiplexer;
    DioramaGame game;

    Editor editor;
    Vector3 cameraLastDragWorldPos;
    boolean takingScreenshot;

    public DemoScreen(DioramaGame game) {
        this.game = game;

        editor = new Editor();
        gameObjects = new GameObjects(editor.getHistory());

        atlas = game.getAtlas();
        defaultCursorRegion = atlas.findRegion("cursor");
        floorRegion = atlas.findRegion("floor");
        shadowRegion = atlas.findRegion("shadow");
        shadowsAtlas = game.getShadowsAtlas();
        decalBatch = new MinimalisticDecalBatch();

        initCamera();
        loadPrototypes();

        hud = new Hud(game, gameObjectPrototypes, editor) {
            @Override
            public void onSelectedItemRegion(GameObjectPrototype prototype) {
                editor.setMode(Editor.Mode.PLACE);
            }
        };

        spriteBatch = new SpriteBatch();
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                Sprite sprite = new Sprite(floorRegion);
                sprite.setSize(1, 1);
                sprite.setPosition(x, y);
                floorSprites.add(sprite);
            }
        }

        cursor = new Cursor(editor, defaultCursorRegion, GRID_SIZE);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(hud);
    }

    private void loadPrototypes() {
        Array<String> blacklist = new Array<>();
        blacklist.add("cursor");
        blacklist.add("selector_background");

        Array<TextureAtlas.AtlasRegion> regions = atlas.getRegions();
        TextureAtlas.AtlasRegion shadowRegion;
        for (TextureAtlas.AtlasRegion region : regions) {
            if (blacklist.contains(region.name, false)) {
                continue;
            }

            shadowRegion = shadowsAtlas.findRegion(region.name);
            gameObjectPrototypes.add(new GameObjectPrototype(region, shadowRegion));
        }
    }

    private void initCamera() {
        float height = 20;
        double ratio = (double) Gdx.graphics.getWidth() / (double) Gdx.graphics.getHeight();
        float width = (float) (height * ratio);
        camera = new PerspectiveCamera(50, width, height);
        camera.position.set(GRID_SIZE / 2.f, -2, 5);
        camera.near = 0.1f;
        camera.far = 300;
        camera.lookAt(GRID_SIZE / 2.f, 4, 0);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.end();

        spriteBatch.begin();
        for (Sprite floorSprite : floorSprites) {
            floorSprite.draw(spriteBatch);
        }
        //TODO zjistit jestli nestaci jeden loop, zalezi jak decalbatch flushuje
        gameObjects.drawShadows(spriteBatch);
        spriteBatch.end();

        gameObjects.drawObjects(decalBatch);

        if (!takingScreenshot) {
            cursor.draw(decalBatch);
        }

        decalBatch.render(camera);

        hud.act();
        if (!takingScreenshot) {
            hud.draw();
        } else {
            takingScreenshot = false;
            saveScreenshot();
        }
    }

    // https://github.com/libgdx/libgdx/wiki/Taking-a-Screenshot
    private void saveScreenshot() {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

        // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
        for (int i = 4; i < pixels.length; i += 4) {
            pixels[i - 1] = (byte) 255;
        }

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(Gdx.files.external("Pictures/PixelLab-" + new Date(TimeUtils.millis()).toString() + ".png"), pixmap);
        pixmap.dispose();
    }

    private void placeCurrentObjectAtCursorPosition() {
        gameObjects.add(editor.getCurrentlySelectedPrototype().createAt(cursor.getPosition()));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //let hud handle this if event occurred on top of menu
        if (hud.isVisible() && screenX >= hud.getMenuScreenPosition().x) {
            return false;
        }

        lastDragScreenPos.set(screenX, screenY);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                Vector3 intersection;
                Ray ray = camera.getPickRay(screenX, screenY);
                intersection = ray.direction.cpy().add(ray.origin);
                Intersector.intersectRayPlane(ray,
                        new Plane(Vector3.Z, Vector3.X),
                        intersection);

                cameraLastDragWorldPos = intersection;
                cameraLastDragWorldPos.z = 0;

                return true;
            } else if (editor.isMode(Editor.Mode.PLACE)) {
                if (cursor.isPlacingItemAllowed() && editor.hasSelectedPrototype()) {
                    placeCurrentObjectAtCursorPosition();

                    return true;
                }
            } else if (editor.isMode(Editor.Mode.DELETE)) {
                GameObject gameObject = findDecalByScreenCoordinates(screenX, screenY);
                if (gameObject != null) {
                    gameObjects.remove(gameObject);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.X:
                editor.setMode(Editor.Mode.DELETE);
                return true;
            case Input.Keys.F:
                takingScreenshot = true;
                return true;
            case Input.Keys.ESCAPE:
                editor.setMode(Editor.Mode.PLACE);
                return true;
            case Input.Keys.T:
                hud.toggle();
                return true;
            case Input.Keys.U:
                editor.getHistory().undo();
                return true;
            case Input.Keys.R:
                editor.getHistory().redo();
                return true;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 intersection;
        Ray ray = camera.getPickRay(screenX, screenY);
        intersection = ray.direction.cpy().add(ray.origin);
        Intersector.intersectRayPlane(ray,
                new Plane(Vector3.Z, Vector3.X),
                intersection);

        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.translate(Vector3.Z.cpy().scl(Gdx.input.getDeltaY() * PAN_PER_PIXEL).add(
                        camera.direction.cpy().rotate(camera.up, 90).nor().scl(Gdx.input.getDeltaX() * PAN_PER_PIXEL)));
            } else {
                camera.rotateAround(camera.position, Vector3.Z, (screenX - lastDragScreenPos.x) * DEGREES_PER_PIXEL);
                camera.rotateAround(camera.position, camera.direction.cpy().rotate(camera.up, -90), (screenY - lastDragScreenPos.y) * DEGREES_PER_PIXEL);
                lastDragScreenPos.set(screenX, screenY);
            }
        } else if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                Vector3 transform = cameraLastDragWorldPos.cpy().sub(intersection);
                transform.z = 0; //TODO: pri dlouhym dragu blbne

                // fix rychlyho posouvani kdyz se klikne daleko
                if (transform.len() > TRANSLATION_LIMIT) {//TODO: zkontrolovat
                    transform.nor().scl(TRANSLATION_LIMIT);
                }
                camera.translate(transform);
                cameraLastDragWorldPos = intersection.add(transform);
                cameraLastDragWorldPos.z = 0;
            }
        }

        //let hud handle this if event occurred on top of menu
        if (hud.isVisible() && screenX >= hud.getMenuScreenPosition().x) {
            cursor.hide();
            return false;
        } else {
            // fixes item jitter when moving/rotating camera
            camera.update();
            cursor.show();
            moveCursorTo(new Vector3(screenX, screenY, 0));
            return true;
        }
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //let hud handle this if event occurred on top of menu
        if (hud.isVisible() && screenX >= hud.getMenuScreenPosition().x) {
            cursor.hide();
        } else {
            cursor.show();
        }

        moveCursorTo(new Vector3(screenX, screenY, 0));
        return true;
    }

    public GameObject findDecalByScreenCoordinates(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);

        return gameObjects.findIntersectingWithRay(ray);
    }

    private void moveCursorTo(Vector3 screenCoordinates) {
        Vector3 intersection = new Vector3();
        Ray ray = camera.getPickRay(screenCoordinates.x, screenCoordinates.y);

        Intersector.intersectRayPlane(ray,
                new Plane(Vector3.Z, Vector3.X),
                intersection);

        cursor.moveTo(intersection);
    }

    @Override
    public boolean scrolled(int amount) {
        //let hud handle this if event occurred on top of menu
        if (hud.isVisible() && Gdx.input.getX() >= hud.getMenuScreenPosition().x) {
            return false;
        }

        camera.position.add(camera.direction.cpy().nor().scl(-SCROLL_RATIO * amount));//TODO FIX ztratu focusu pri kliknuti na pane
        moveCursorTo(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        return true;
    }
}

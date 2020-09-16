package cz.shroomware.diorama.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
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
import cz.shroomware.diorama.editor.GameObjectsTool;
import cz.shroomware.diorama.editor.FloorTool;
import cz.shroomware.diorama.engine.level.GameObject;
import cz.shroomware.diorama.engine.level.GameObjectPrototype;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.ui.Hud;

public class EditorScreen extends BaseScreen {
    protected static final float PAN_PER_PIXEL = 0.02f;
    protected static final float SCROLL_RATIO = 0.4f;
    protected static final float DEGREES_PER_PIXEL = 0.2f;
    protected static final float TRANSLATION_LIMIT = 3;
    protected static final float MAX_CAM_DIST_FROM_GRID = 8;
    protected Editor editor;
    protected FloorTool floorTool;
    protected GameObjectsTool gameObjectsTool;
    protected InputMultiplexer inputMultiplexer;
    protected TextureAtlas atlas;
    protected TextureAtlas shadowsAtlas;
    protected TextureRegion defaultCursorRegion;
    protected Cursor cursor;
    protected Hud hud;
    protected Prototypes gameObjectPrototypes;
    protected Vector2 lastDragScreenPos = new Vector2();
    protected Vector3 cameraLastDragWorldPos;
    protected GameObject currentlyHighlightedObject;
    protected boolean takingScreenshot;
    protected boolean showAddRemoveMessages = false;
    protected boolean preventSave = false; //TODO: this is a workaround for not saving player from preview etc.
    boolean dragging = false;
    Level level;
    float time = 0;

    public EditorScreen(DioramaGame game, String filename) {
        super(game);
        editor = new Editor(filename);

        atlas = game.getAtlas();
        defaultCursorRegion = atlas.findRegion("cursor");
        shadowsAtlas = game.getShadowsAtlas();

        gameObjectPrototypes = new Prototypes();
        loadPrototypes();

        level = new Level(filename, gameObjectPrototypes, atlas);
        updateBackgorundColor(level);
        initCamera(level);

        floorTool = new FloorTool(level.getFloor(), editor.getHistory());
        gameObjectsTool = new GameObjectsTool(level.getGameObjects(), editor.getHistory());

        cursor = new Cursor(editor, level, defaultCursorRegion);

        hud = new Hud(game, gameObjectPrototypes, editor, level);

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
            gameObjectPrototypes.addGameObjectProtoype(new GameObjectPrototype(region, shadowRegion));
        }
    }

    @Override
    public void show() {
        preventSave = false;
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        time += delta;
        super.render(delta);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);

        spriteBatch.getShader().setUniformf("u_camera_pos", camera.position);
        spriteBatch.getShader().setUniformf("u_background_color", backgroundColor);
        spriteBatch.getShader().setUniformf("time", time / 10f);
        level.draw(spriteBatch, decalBatch, delta);

        if (editor.getMode() == Editor.Mode.DELETE) {
            selectObjectUnderCursor(Gdx.input.getX(), Gdx.input.getY());
        }

        if (takingScreenshot) {
            if (currentlyHighlightedObject != null) {
                currentlyHighlightedObject.setSelected(false);
            }
        } else {
            cursor.draw(spriteBatch, decalBatch);
        }
        spriteBatch.end();
        decalBatch.render(camera, backgroundColor, time / 10f);

        hud.setDirty(level.isDirty());
        hud.act();
        if (!takingScreenshot) {
            hud.draw();
        } else {
            takingScreenshot = false;
            saveScreenshot();
            hud.showMessage("Screenshot saved");
        }

        if (currentlyHighlightedObject != null) {
            currentlyHighlightedObject.setSelected(false);
            currentlyHighlightedObject = null;
        }
    }

    protected void clampCameraPos(Camera camera) {
        camera.position.x = MathUtils.clamp(camera.position.x, -MAX_CAM_DIST_FROM_GRID, level.getSize() + MAX_CAM_DIST_FROM_GRID);
        camera.position.y = MathUtils.clamp(camera.position.y, -MAX_CAM_DIST_FROM_GRID, level.getSize() + MAX_CAM_DIST_FROM_GRID);
        camera.position.z = MathUtils.clamp(camera.position.z, 0.4f, 20);
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
        gameObjectsTool.addObject(editor.getCurrentlySelectedPrototype().createAtCursor(cursor));
        if (showAddRemoveMessages) {
            hud.showMessage("ADD " + editor.getCurrentlySelectedPrototype().getName());
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        dragging = false;
        //let hud handle this if event occurred on top of menu
        Vector2 pos = new Vector2(screenX, screenY);
        pos = hud.screenToStageCoordinates(pos);
        if (hud.isVisible() && hud.hit(pos.x, pos.y, false) != null) {
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

                dragging = true;
                cameraLastDragWorldPos = intersection;
                cameraLastDragWorldPos.z = 0;

                return true;
            } else if (editor.isMode(Editor.Mode.ITEM)) {
                if (cursor.isPlacingItemAllowed() && editor.hasSelectedPrototype()) {
                    placeCurrentObjectAtCursorPosition();

                    return true;
                }
            } else if (editor.isMode(Editor.Mode.DELETE)) {
                GameObject gameObject = findDecalByScreenCoordinates(screenX, screenY);
                if (gameObject != null) {
                    gameObjectsTool.removeObject(gameObject);
                    if (showAddRemoveMessages) {
                        hud.showMessage("DEL " + gameObject.getName());
                    }
                }
                return true;
            } else if (editor.isMode(Editor.Mode.TILE)) {
                if (editor.getCurrentlySelectedPrototype() != null) {
                    Vector3 intersection;
                    Ray ray = camera.getPickRay(screenX, screenY);
                    intersection = ray.direction.cpy().add(ray.origin);
                    Intersector.intersectRayPlane(ray,
                            new Plane(Vector3.Z, Vector3.X),
                            intersection);

                    floorTool.setTileRegion(intersection.x, intersection.y, editor.getPrototypeObjectRegion());
                }
            } else if (editor.isMode(Editor.Mode.TILE_BUCKET)) {
                if (editor.getCurrentlySelectedPrototype() != null) {
                    Vector3 intersection;
                    Ray ray = camera.getPickRay(screenX, screenY);
                    intersection = ray.direction.cpy().add(ray.origin);
                    Intersector.intersectRayPlane(ray,
                            new Plane(Vector3.Z, Vector3.X),
                            intersection);

                    floorTool.tileRegionBucketAt(intersection.x, intersection.y, editor.getPrototypeObjectRegion());
                }
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
                editor.setMode(Editor.Mode.ITEM);
                return true;
            case Input.Keys.T:
                hud.toggle();
                return true;
            case Input.Keys.U:
                String undoText = editor.getHistory().undo();
                if (undoText != null) {
                    hud.showMessage("Undo: " + undoText);
                } else {
                    hud.showMessage("No more steps to undo");
                }
                return true;
            case Input.Keys.R:
                String redoText = editor.getHistory().redo();
                if (redoText != null) {
                    hud.showMessage("Redo: " + redoText);
                } else {
                    hud.showMessage("No more steps to redo");
                }
                return true;
            case Input.Keys.S:
                if (save()) {
                    hud.showMessage("Saved as " + level.getFilename());
                } else {
                    hud.showMessage("NOT saved as " + level.getFilename());
                }
                return true;
            case Input.Keys.L:
                if (level.loadIfExists(gameObjectPrototypes, atlas)) {
                    hud.showMessage("Loaded " + level.getFilename());
                } else {
                    hud.showMessage("FAILED to load " + level.getFilename());
                }
                return true;
            case Input.Keys.Z:
                cursor.rotateY(90);
                return true;
            case Input.Keys.TAB:
                editor.setNextMode();
                return true;
            case Input.Keys.P:
                preventSave = true;
                game.openGamePreview(level);
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.SHIFT_LEFT:
                dragging = false;
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
                clampCameraPos(camera);
                lastDragScreenPos.set(screenX, screenY);
            } else if (!dragging) {
                camera.rotateAround(camera.position, Vector3.Z, (screenX - lastDragScreenPos.x) * DEGREES_PER_PIXEL);
                camera.rotateAround(camera.position, camera.direction.cpy().rotate(camera.up, -90), (screenY - lastDragScreenPos.y) * DEGREES_PER_PIXEL);
                lastDragScreenPos.set(screenX, screenY);
            }
        } else if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && dragging) {
                Vector3 transform = cameraLastDragWorldPos.cpy().sub(intersection);
                transform.z = 0; //TODO: pri dlouhym dragu blbne

                // fix rychlyho posouvani kdyz se klikne daleko
                if (transform.len() > TRANSLATION_LIMIT) {//TODO: zkontrolovat
                    transform.nor().scl(TRANSLATION_LIMIT);
                }
                camera.translate(transform);
                clampCameraPos(camera);

                cameraLastDragWorldPos = intersection.add(transform);
                cameraLastDragWorldPos.z = 0;
            }
        }

        //let hud handle this if event occurred on top of menu
        Vector2 pos = new Vector2(screenX, screenY);
        pos = hud.screenToStageCoordinates(pos);
        if (hud.isVisible() && hud.hit(pos.x, pos.y, false) != null) {
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
        Vector2 pos = new Vector2(screenX, screenY);
        pos = hud.screenToStageCoordinates(pos);
        if (hud.isVisible() && hud.hit(pos.x, pos.y, false) != null) {
            cursor.hide();
        } else {
            cursor.show();
        }

        moveCursorTo(new Vector3(screenX, screenY, 0));
        return true;
    }

    protected void selectObjectUnderCursor(int screenX, int screenY) {
        if (currentlyHighlightedObject != null) {
            currentlyHighlightedObject.setSelected(false);
        }

        currentlyHighlightedObject = findDecalByScreenCoordinates(screenX, screenY);

        if (currentlyHighlightedObject != null) {
            currentlyHighlightedObject.setSelected(true);
        }
    }

    public GameObject findDecalByScreenCoordinates(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);

        return level.findIntersectingWithRay(ray, camera.position);
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
        Vector2 pos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        pos = hud.screenToStageCoordinates(pos);
        if (hud.isVisible() && hud.hit(pos.x, pos.y, false) != null) {
            return false;
        }

        camera.position.add(camera.direction.cpy().nor().scl(-SCROLL_RATIO * amount));//TODO FIX ztratu focusu pri kliknuti na pane
        clampCameraPos(camera);

        moveCursorTo(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        return true;
    }

    public boolean save() {
        return level.save(false);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = calculateCameraViewportWidth();
        camera.viewportHeight = calculateCameraViewportHeight();
        camera.update();

        hud.resize(width, height);
    }

    @Override
    public void hide() {
        if (!preventSave) {
            save();
        }

        Gdx.app.error("hide", "editor");
        super.hide();
    }
}

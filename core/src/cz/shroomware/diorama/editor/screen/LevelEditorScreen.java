package cz.shroomware.diorama.editor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Collection;
import java.util.Date;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.Cursor;
import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.editor.EditorTool;
import cz.shroomware.diorama.editor.ui.Hud;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.GameObjects;
import cz.shroomware.diorama.engine.level.portal.MetaPortal;
import cz.shroomware.diorama.engine.screen.BaseLevelScreen;

public class LevelEditorScreen extends BaseLevelScreen {
    protected static final float PAN_PER_PIXEL = 0.02f;
    protected static final float SCROLL_RATIO = 0.4f;
    protected static final float DEGREES_PER_PIXEL = 0.2f;
    protected static final float TRANSLATION_LIMIT = 1.4f;
    protected static final float MAX_CAM_DIST_FROM_GRID = 8;
    protected EditorEngineGame game;
    protected Editor editor;
    protected EditorTool editorTool;
    protected EditorResources resources;
    protected InputMultiplexer inputMultiplexer;
    protected TextureAtlas shadowAtlas;
    protected TextureAtlas.AtlasRegion defaultCursorRegion;
    protected Cursor cursor;
    protected Hud hud;
    protected OrthographicCamera screenCamera;
    protected Vector2 lastDragScreenPos = new Vector2();
    protected Vector3 cameraLastDragWorldPos;
    protected GameObject currentlyHighlightedObject;
    protected boolean takingScreenshot;
    protected boolean showAddRemoveMessages = false;
    boolean dragging = false;
    float time = 0;

    public LevelEditorScreen(EditorEngineGame game, Level level) {
        super(game.getResources(), level);
        this.game = game;
        this.level = level;

        editor = new Editor();

        resources = game.getResources();
        defaultCursorRegion = resources.getObjectAtlas().findRegion("cursor");
        shadowAtlas = resources.getShadowAtlas();

        updateBackgroundColor(level);

        screenCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        editorTool = new EditorTool(level, editor);

        cursor = new Cursor(editor, resources, level, defaultCursorRegion);

        hud = new Hud(game, editor, level);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud);
        inputMultiplexer.addProcessor(this);
    }

    @Override
    public void show() {
        Gdx.graphics.setTitle("Level Editor - " + level.getMetaLevel().getName());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void drawWorld(float delta) {
        time += delta;

        PerspectiveCamera camera = level.getCamera();
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);

        if (editor.getMode() == Editor.Mode.DELETE
                || editor.getMode() == Editor.Mode.ID_ASSIGN) {
            selectObjectUnderCursor(Gdx.input.getX(), Gdx.input.getY());
        }

        if (editor.isMode(Editor.Mode.ITEM_MOVE) && !editor.isMovingObject()) {
            selectObjectUnderCursor(Gdx.input.getX(), Gdx.input.getY());
        }

        spriteBatch.getShader().setUniformf("u_camera_pos", camera.position);
        spriteBatch.getShader().setUniformf("u_background_color", backgroundColor);
        spriteBatch.getShader().setUniformf("time", time / 10f);

        level.update(delta);
        level.draw(spriteBatch, decalBatch, delta);

        if (takingScreenshot) {
            if (currentlyHighlightedObject != null) {
                currentlyHighlightedObject.setSelected(false);
            }
        } else {
            cursor.show();
            cursor.draw(spriteBatch, decalBatch);
        }
        spriteBatch.end();
        decalBatch.render(camera, backgroundColor, time / 10f);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (editor.getShowLabels()) {
            drawIdLabels();
        }

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

    protected void drawIdLabels() {
        PerspectiveCamera camera = level.getCamera();

        spriteBatch.setShader(resources.getDfShader());
        spriteBatch.setProjectionMatrix(screenCamera.combined);

        spriteBatch.begin();

        GameObjects gameObjects = level.getGameObjects();
        BitmapFont font = resources.getFont();
        float size = 20;

        for (int i = 0; i < gameObjects.getSize(); i++) {
            GameObject object = gameObjects.get(i);

            Vector3 position = object.getPosition().cpy();
            Plane plane = new Plane(camera.direction, camera.position);
            if (plane.testPoint(position) == Plane.PlaneSide.Back) {
                continue;
            }

            position = camera.project(position);

            if (object.getIdentifier().isSet()) {
                resources.getSlotDrawable().draw(spriteBatch,
                        position.x - size / 2,
                        position.y - size / 2,
                        size,
                        size);

                font.setColor(Color.BLACK);
                font.draw(spriteBatch,
                        object.getIdentifier().getIdString(),
                        position.x + 2,
                        position.y);
                font.draw(spriteBatch,
                        object.getIdentifier().getIdString(),
                        position.x - 2,
                        position.y);
                font.draw(spriteBatch,
                        object.getIdentifier().getIdString(),
                        position.x,
                        position.y + 2);
                font.draw(spriteBatch,
                        object.getIdentifier().getIdString(),
                        position.x,
                        position.y - 2);

                font.setColor(Color.WHITE);
                font.draw(spriteBatch,
                        object.getIdentifier().getIdString(),
                        position.x,
                        position.y);
            }
        }

        spriteBatch.end();
    }

    protected void clampCameraPos(Camera camera) {
        camera.position.x = MathUtils.clamp(camera.position.x, -MAX_CAM_DIST_FROM_GRID, level.getWidth() + MAX_CAM_DIST_FROM_GRID);
        camera.position.y = MathUtils.clamp(camera.position.y, -MAX_CAM_DIST_FROM_GRID, level.getHeight() + MAX_CAM_DIST_FROM_GRID);
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
        if (cursor.isPlacingItemAllowed() && editor.hasSelectedPrototype()) {
            GameObject gameObject = editor.getCurrentlySelectedPrototype().createAt(cursor.getPosition(), level.getBoxFactory());
            gameObject.setRotation(cursor.getRotation());
            editorTool.addObject(gameObject, true);
            if (showAddRemoveMessages) {
                hud.showMessage("ADD " + editor.getCurrentlySelectedPrototype().getName());
            }
        }
    }

    Plane floorPlane = new Plane(Vector3.Z, Vector3.Zero);

    @Override
    public boolean keyDown(int keycode) {
        if (super.keyDown(keycode)) {
            return true;
        }
        //TODO make every function working through GUI
        switch (keycode) {
            case Input.Keys.L:
                editor.toggleLabels();
                return true;
            case Input.Keys.D:
                editor.toggleDelete();
                return true;
            case Input.Keys.G:
                editor.setMode(Editor.Mode.TILE_BUCKET);
                return true;
            case Input.Keys.B:
                editor.setMode(Editor.Mode.TILE);
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
            case Input.Keys.W:
                if (save()) {
                    hud.showMessage("Saved as " + level.getMetaLevel().getName());
                } else {
                    hud.showMessage("NOT saved as " + level.getMetaLevel().getName());
                }
                return true;
//            case Input.Keys.L:
//                if (level.loadIfExists(gameObjectPrototypes,l resources.getObjectAtlas())) {
//                    hud.showMessage("Loaded " + level.getFilename());
//                } else {
//                    hud.showMessage("FAILED to load " + level.getFilename());
//                }
//                return true;
            case Input.Keys.Z:
                if (editor.isMovingObject()) {
                    editor.getMovedObject().rotateY(90);
                } else {
                    cursor.rotateY(90);
                }
                return true;
            case Input.Keys.TAB:
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    editor.setPrevMode();
                } else {
                    editor.setNextMode();
                }
                return true;
            case Input.Keys.P:
                save();
                Collection<MetaPortal> portals = level.getMetaLevel().getMetaPortals().getValues();
                MetaPortal entryPortal = null;
                for (MetaPortal candidate : portals) {
                    if (entryPortal == null) {
                        entryPortal = candidate;
                    } else {
                        if (candidate.getIdentifier().getIdString().equals(Utils.START_PORTAL)) {
                            entryPortal = candidate;
                        }
                    }
                }

                if (entryPortal != null) {
                    game.openLevel(entryPortal);
                } else {
                    game.openLevel(level.getMetaLevel(), level.getWidth() / 2f, 0);
                }

                return true;
            case Input.Keys.V:
                level.dumpLogic();
                return true;
            case Input.Keys.SEMICOLON:
                game.openLogicEditor(level.getMetaLevel(), level.getLogic());
                return true;
            case Input.Keys.S:
                editor.toggleHardSnap();
                return true;
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

    Vector3 offset = new Vector3();

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        PerspectiveCamera camera = level.getCamera();

        Vector3 intersection = getRayIntersectionWithFloor(screenX, screenY);

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
            }
        }

        // Let hud handle this if event occurred on top of menu
        Vector2 pos = new Vector2(screenX, screenY);
        pos = hud.screenToStageCoordinates(pos);
        if (hud.isVisible() && hud.hit(pos.x, pos.y, false) != null) {
            cursor.hide();
            return false;
        } else {
            // Fixes item jitter when moving/rotating camera
            camera.update();
            cursor.show();
            cursor.setPosition(intersection.x, intersection.y);
        }

        if (editor.isMode(Editor.Mode.ITEM_MOVE) && editor.isMovingObject()) {
            if (editor.isMovingObject()) {
                intersection = new Vector3();

                Ray ray = camera.getPickRay(screenX, screenY);
                ray.origin.z -= offset.z;
                Intersector.intersectRayPlane(ray, floorPlane, intersection);

                editorTool.moveObject(intersection.x + offset.x,
                        intersection.y + offset.y,
                        editor.getMovedObject());
                cursor.setPosition(intersection.x + offset.x, intersection.y + offset.y);
            }
        }

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        PerspectiveCamera camera = level.getCamera();

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
                        floorPlane,
                        intersection);

                dragging = true;
                cameraLastDragWorldPos = intersection;
                cameraLastDragWorldPos.z = 0;

                return true;
            } else if (editor.isMode(Editor.Mode.ITEM)) {
                placeCurrentObjectAtCursorPosition();
                return true;
            } else if (editor.isMode(Editor.Mode.DELETE)) {
                GameObject gameObject = findDecalByScreenCoordinates(screenX, screenY);
                if (gameObject != null) {
                    editorTool.removeObject(gameObject, true);
                    if (showAddRemoveMessages) {
                        hud.showMessage("DEL " + gameObject.getName());
                    }
                }
                return true;
            } else if (editor.isMode(Editor.Mode.ID_ASSIGN)) {
                GameObject gameObject = findDecalByScreenCoordinates(screenX, screenY);
                if (gameObject != null) {
                    hud.openIdAssignDialog(level.getGameObjects(), gameObject);
                }
                return true;
            } else if (editor.isMode(Editor.Mode.TILE)) {
                if (editor.getCurrentlySelectedPrototype() != null) {
                    Vector3 intersection = getRayIntersectionWithFloor(screenX, screenY);
                    editorTool.setTileRegion(intersection.x, intersection.y, editor.getPrototypeIcon());
                    return true;
                }
            } else if (editor.isMode(Editor.Mode.TILE_BUCKET)) {
                if (editor.getCurrentlySelectedPrototype() != null) {
                    Vector3 intersection = getRayIntersectionWithFloor(screenX, screenY);
                    editorTool.tileRegionBucketAt(intersection.x, intersection.y, editor.getPrototypeIcon());
                    return true;
                }
            } else if (editor.isMode(Editor.Mode.ITEM_MOVE)) {
                if (editor.isMovingObject()) {
                    editor.stopMove();
                    currentlyHighlightedObject = null;

                    return true;
                } else {
                    selectObjectUnderCursor(screenX, screenY);
                    if (currentlyHighlightedObject == null) {
                        return false;
                    }

                    Ray ray = camera.getPickRay(screenX, screenY);
                    Plane objectPlane = new Plane(camera.direction, currentlyHighlightedObject.getPosition());

                    Vector3 intersection = new Vector3();
                    Intersector.intersectRayPlane(ray, objectPlane, intersection);

                    offset.x = currentlyHighlightedObject.getPosition().x - intersection.x;
                    offset.y = currentlyHighlightedObject.getPosition().y - intersection.y;
                    offset.z = intersection.z;

                    editor.setMovedObject(currentlyHighlightedObject);

                    return true;
                }
            } else if (editor.isMode(Editor.Mode.PORTAL)) {
                Vector3 intersection = getRayIntersectionWithFloor(screenX, screenY);
                level.getPortals().create(intersection.x,
                        intersection.y,
                        1,
                        1,
                        "" + MathUtils.random(0, 100));
            }
        }

        return false;
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
        PerspectiveCamera camera = level.getCamera();

        Ray ray = camera.getPickRay(screenX, screenY);

        return level.findIntersectingWithRay(ray, camera);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        PerspectiveCamera camera = level.getCamera();

        // Let hud handle this if event occurred on top of menu
        Vector2 pos = new Vector2(screenX, screenY);
        pos = hud.screenToStageCoordinates(pos);
        if (hud.isVisible() && hud.hit(pos.x, pos.y, false) != null) {
            hud.setScrollFocus(pos.x, pos.y);
            cursor.hide();
        } else {
            hud.cancelScrollFocus();
            cursor.show();
        }

        if (editor.isMode(Editor.Mode.ITEM_MOVE)) {
            if (editor.isMovingObject()) {

                Ray ray = camera.getPickRay(screenX, screenY);
                ray.origin.z -= offset.z;
                Vector3 intersection = new Vector3();
                Intersector.intersectRayPlane(ray, floorPlane, intersection);

                editorTool.moveObject(intersection.x + offset.x,
                        intersection.y + offset.y,
                        editor.getMovedObject());
                cursor.setPosition(intersection.x + offset.x, intersection.y + offset.y);
            }
        } else {
            Vector3 intersection = getRayIntersectionWithFloor(screenX, screenY);
            cursor.setPosition(intersection.x, intersection.y);
        }
        return true;
    }

    private Vector3 getRayIntersectionWithFloor(int x, int y) {
        PerspectiveCamera camera = level.getCamera();

        Vector3 intersection = new Vector3();
        Ray ray = camera.getPickRay(x, y);

        Intersector.intersectRayPlane(ray, floorPlane, intersection);
        return intersection;
    }

    @Override
    public boolean scrolled(int amount) {
        PerspectiveCamera camera = level.getCamera();

        Vector2 pos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        pos = hud.screenToStageCoordinates(pos);
        if (hud.isVisible() && hud.hit(pos.x, pos.y, false) != null) {
            return false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (editor.isMode(Editor.Mode.ITEM)) {
                if (amount > 0) {
                    cursor.decrementZOffset();
                } else {
                    cursor.incrementZOffset();
                }

                cursor.updateZ();
                return true;
            } else if (editor.isMode(Editor.Mode.ITEM_MOVE) && editor.isMovingObject()) {
                if (amount > 0) {
                    editorTool.transformObjectZ(1f / Utils.PIXELS_PER_METER, editor.getMovedObject());
                } else {
                    editorTool.transformObjectZ(-1f / Utils.PIXELS_PER_METER, editor.getMovedObject());
                }
                return true;
            }
        }

        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();

        if (editor.getHardSnap() && (editor.isMode(Editor.Mode.ITEM)
                || editor.isMode(Editor.Mode.ITEM_MOVE))) {
            if (Gdx.input.isKeyPressed(Input.Keys.X)) {
                float oldXOffset = editor.getSnapOffsetX();

                if (amount > 0) {
                    editor.decrementXOffset();
                } else if (amount < 0) {
                    editor.incrementXOffset();
                }

                if (editor.isMovingObject()) {
                    editorTool.transformObject(
                            editor.getSnapOffsetX() - oldXOffset,
                            0,
                            editor.getMovedObject());
                }

                if (editor.isMode(Editor.Mode.ITEM)) {
                    Vector3 intersection = getRayIntersectionWithFloor(screenX, screenY);
                    cursor.setPosition(intersection.x, intersection.y);
                }
                return true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.Y)) {
                float oldYOffset = editor.getSnapOffsetY();

                if (amount > 0) {
                    editor.decrementYOffset();
                } else if (amount < 0) {
                    editor.incrementYOffset();
                }

                if (editor.isMovingObject()) {
                    editorTool.transformObject(0,
                            editor.getSnapOffsetY() - oldYOffset,
                            editor.getMovedObject());
                }

                if (editor.isMode(Editor.Mode.ITEM)) {
                    Vector3 intersection = getRayIntersectionWithFloor(screenX, screenY);
                    cursor.setPosition(intersection.x, intersection.y);
                }
                return true;
            }
        }

        camera.position.add(camera.direction.cpy().nor().scl(-SCROLL_RATIO * amount));//TODO FIX ztratu focusu pri kliknuti na pane
        clampCameraPos(camera);

        if (editor.isMode(Editor.Mode.ITEM_MOVE) && editor.isMovingObject()) {
            if (editor.isMovingObject()) {

                Ray ray = camera.getPickRay(screenX, screenY);
                ray.origin.z -= offset.z;
                Vector3 intersection = new Vector3();
                Intersector.intersectRayPlane(ray, floorPlane, intersection);

                editorTool.moveObject(intersection.x + offset.x,
                        intersection.y + offset.y,
                        editor.getMovedObject());
                cursor.setPosition(intersection.x + offset.x, intersection.y + offset.y);
            }
        } else {
            //TODO: improve this part
            Vector3 intersection = getRayIntersectionWithFloor(screenX, screenY);
            cursor.setPosition(intersection.x, intersection.y);
        }
        return true;
    }

    public boolean save() {
        return level.save(false);
    }

    @Override
    public void resize(int width, int height) {
        PerspectiveCamera camera = level.getCamera();
        if (camera != null) {
            camera.viewportWidth = Utils.calculateCameraViewportWidth();
            camera.viewportHeight = Utils.calculateCameraViewportHeight();
            camera.update();
        }

        screenCamera.viewportWidth = width;
        screenCamera.viewportHeight = height;
        screenCamera.position.set(((float) width) / 2, ((float) height / 2), 0);
        screenCamera.update();

        hud.resize(width, height);
    }

    @Override
    public void hide() {
        save();

        Gdx.app.log("Editor", "hide()");
        super.hide();
    }
}

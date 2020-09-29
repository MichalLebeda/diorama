package cz.shroomware.diorama.editor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
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

import java.util.Date;

import cz.shroomware.diorama.editor.Cursor;
import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.editor.EditorTool;
import cz.shroomware.diorama.editor.ui.Hud;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.engine.level.object.GameObject;
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
    protected Prototypes gameObjectPrototypes;
    protected Vector2 lastDragScreenPos = new Vector2();
    protected Vector3 cameraLastDragWorldPos;
    protected GameObject currentlyHighlightedObject;
    protected boolean takingScreenshot;
    protected boolean showAddRemoveMessages = false;
    boolean dragging = false;
    float time = 0;

    public LevelEditorScreen(EditorEngineGame game, String filename) {
        super(game.getResources());
        this.game = game;

        editor = new Editor(filename);

        resources = game.getResources();
        defaultCursorRegion = resources.getObjectAtlas().findRegion("cursor");
        shadowAtlas = resources.getShadowAtlas();

        gameObjectPrototypes = new Prototypes(resources);

        level = new Level(filename, gameObjectPrototypes, resources);
        updatebackgorundcolor(level);
        initCamera(level);

        editorTool = new EditorTool(level.getFloor(), level.getGameObjects(), editor);

        cursor = new Cursor(editor, resources, level, defaultCursorRegion);

        hud = new Hud(game, gameObjectPrototypes, editor, level);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud);
        inputMultiplexer.addProcessor(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void drawWorld(float delta) {
        time += delta;

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);

        spriteBatch.getShader().setUniformf("u_camera_pos", camera.position);
        spriteBatch.getShader().setUniformf("u_background_color", backgroundColor);
        spriteBatch.getShader().setUniformf("time", time / 10f);
        level.step(delta);
        level.draw(spriteBatch, decalBatch, delta);

        if (editor.getMode() == Editor.Mode.DELETE || editor.getMode() == Editor.Mode.ID_ASSIGN) {
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
    }

    @Override
    public void render(float delta) {
        super.render(delta);

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
        if (cursor.isPlacingItemAllowed() && editor.hasSelectedPrototype()) {
            GameObject gameObject = editor.getCurrentlySelectedPrototype().createAt(cursor.getPosition(), level.getBoxFactory());
            gameObject.setRotation(cursor.getRotation());
            editorTool.addObject(gameObject, true);
            if (showAddRemoveMessages) {
                hud.showMessage("ADD " + editor.getCurrentlySelectedPrototype().getName());
            }
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
                    Vector3 intersection;
                    Ray ray = camera.getPickRay(screenX, screenY);
                    intersection = ray.direction.cpy().add(ray.origin);
                    Intersector.intersectRayPlane(ray,
                            new Plane(Vector3.Z, Vector3.X),
                            intersection);

                    editorTool.setTileRegion(intersection.x, intersection.y, editor.getPrototypeIcon());
                }
            } else if (editor.isMode(Editor.Mode.TILE_BUCKET)) {
                if (editor.getCurrentlySelectedPrototype() != null) {
                    Vector3 intersection;
                    Ray ray = camera.getPickRay(screenX, screenY);
                    intersection = ray.direction.cpy().add(ray.origin);
                    Intersector.intersectRayPlane(ray,
                            new Plane(Vector3.Z, Vector3.X),
                            intersection);

                    editorTool.tileRegionBucketAt(intersection.x, intersection.y, editor.getPrototypeIcon());
                }
            }
        }

        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (super.keyDown(keycode)) {
            return true;
        }
        //TODO make every function working through GUI
        switch (keycode) {
            case Input.Keys.X:
                editor.toggleDelete();
                return true;
            case Input.Keys.G:
                editor.setMode(Editor.Mode.TILE_BUCKET);
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
//            case Input.Keys.L:
//                if (level.loadIfExists(gameObjectPrototypes,l resources.getObjectAtlas())) {
//                    hud.showMessage("Loaded " + level.getFilename());
//                } else {
//                    hud.showMessage("FAILED to load " + level.getFilename());
//                }
//                return true;
            case Input.Keys.Z:
                cursor.rotateY(90);
                return true;
            case Input.Keys.TAB:
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    editor.setPrevMode();
                } else {
                    editor.setNextMode();
                }
                return true;
            case Input.Keys.P:
                save();
                game.openGame(level.getFilename(), gameObjectPrototypes);
                return true;
            case Input.Keys.V:
                level.dumpLogic();
                return true;
            case Input.Keys.SEMICOLON:
                game.openLogicEditor(level.getFilename(), level.getLogic());
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
            hud.setScrollFocus(true);
            cursor.hide();
        } else {
            hud.setScrollFocus(false);
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

        if (editor.hasSelectedPrototype() && editor.getCurrentlySelectedPrototype().isAttached()) {
            intersection.x = ((int) intersection.x) + 0.5f;
            intersection.y = ((int) intersection.y) + 0.5f;
//            intersection.z = ((int) intersection.z) + 0.5f;
        }

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
        save();

        Gdx.app.error("hide", "editor");
        super.hide();
    }
}

package cz.shroomware.diorama;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import cz.shroomware.diorama.ui.Hud;

public class DemoScreen implements Screen, InputProcessor {
    private static final float SCROLL_RATIO = 0.4f;
    private static final float RELATIVE_SHADOW_SIZE = 1.2f;
    private static final int GRID_SIZE = 200;
    private static final float DEGREES_PER_PIXEL = 0.2f;
    private static final float TRANSLATION_LIMIT = 1;

    MinimalisticDecalBatch decalBatch;
    SpriteBatch spriteBatch;
    PerspectiveCamera camera;
    TextureAtlas atlas;
    TextureAtlas shadowsAtlas;
    TextureRegion cursorRegion;
    TextureRegion floorRegion;
    TextureRegion shadowRegion;
    Vector2 lastDragScreenPos = new Vector2();
    Array<Decal> decals = new Array<Decal>();
    Array<Sprite> sprites = new Array<Sprite>();
    Decal cursor;
    Hud hud;
    InputMultiplexer inputMultiplexer;

    DemoScreen(DioramaGame game) {
        initCamera();
        atlas = game.getAtlas();
        shadowsAtlas = game.getShadowsAtlas();
        decalBatch = new MinimalisticDecalBatch();
        hud = new Hud(game, atlas) {
            @Override
            public void onSelectedItemRegion(TextureAtlas.AtlasRegion region) {
                updateCursor(region);
            }
        };
        cursorRegion = atlas.findRegion("tree");
        floorRegion = atlas.findRegion("floor");
        shadowRegion = atlas.findRegion("shadow");

        spriteBatch = new SpriteBatch();
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                Sprite sprite = new Sprite(floorRegion);
                sprite.setSize(1, 1);
                sprite.setPosition(x, y);
                sprites.add(sprite);
            }
        }

        cursor = Decal.newDecal(1, 1, cursorRegion, true);
        cursor.rotateX(90);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(hud);
    }

    private void initCamera() {
        float height = 20;
        double ratio = (double) Gdx.graphics.getWidth() / (double) Gdx.graphics.getHeight();
        float width = (float) (height * ratio);
        camera = new PerspectiveCamera(50, width, height);
        camera.position.set(GRID_SIZE / 2.f, -2, 5);
        camera.lookAt(GRID_SIZE / 2.f, 4, 0);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        boolean takingScreenshot = Gdx.input.isKeyJustPressed(Input.Keys.F);
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
//            hud.set TODO:HIDE HUD
        }

        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.end();

        spriteBatch.begin();
        for (Sprite sprite : sprites) {
            sprite.draw(spriteBatch);
        }
        spriteBatch.end();

        for (Decal decal : decals) {
            decalBatch.add(decal);
        }

        if (!takingScreenshot) {
            if (hud.hasSelectedRegion()) {
                decalBatch.add(cursor);
            }
        }

        decalBatch.render(camera);

        hud.act();
        if (!takingScreenshot) {
            hud.draw();
        } else {
            saveScreenshot();
        }
    }

    private void saveScreenshot() {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

// This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
        for (int i = 4; i < pixels.length; i += 4) {
            pixels[i - 1] = (byte) 255;
        }

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(Gdx.files.external("mypixmap.png"), pixmap);
        pixmap.dispose();
    }

    private void placeObject(Decal source) {
        Decal decal = Decal.newDecal(source.getTextureRegion(), true);
        decal.setPosition(source.getPosition());
        decal.setRotation(source.getRotation());
        decal.setWidth(source.getWidth());
        decal.setHeight(source.getHeight());
        decals.add(decal);

        Sprite shadowSprite = new Sprite(shadowsAtlas.findRegion(((TextureAtlas.AtlasRegion) decal.getTextureRegion()).name));
        shadowSprite.setSize(decal.getWidth() * 2, -((float) shadowSprite.getRegionHeight() / (float) shadowSprite.getRegionWidth() * decal.getWidth() * 2));
        shadowSprite.setPosition(decal.getPosition().x - shadowSprite.getWidth() / 2, decal.getPosition().y - shadowSprite.getHeight());
        shadowSprite.setColor(1, 1, 1, 0.8f);
        sprites.add(shadowSprite);
    }

    private void updateCursor(TextureAtlas.AtlasRegion region) {
        cursor.setTextureRegion(region);
        cursor.setWidth(region.getRegionWidth() / 16f);
        cursor.setHeight(region.getRegionHeight() / 16f);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    Vector3 cameraLastDragWorldPos;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //let hud handle this if event occurred on top of menu
        if (screenX >= hud.getMenuScreenPosition().x) {
            return false;
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            lastDragScreenPos.set(screenX, screenY);

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
            }
        } else if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (hud.hasSelectedRegion()) {
                placeObject(cursor);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        //let hud handle this if event occurred on top of menu
        if (screenX >= hud.getMenuScreenPosition().x) {
            cursor.setColor(0, 0, 0, 0);
        } else {
            cursor.setColor(1, 1, 1, 1);
            moveCursorTo(new Vector3(screenX, screenY, 0));
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            Vector3 intersection;
            Ray ray = camera.getPickRay(screenX, screenY);
            intersection = ray.direction.cpy().add(ray.origin);
            Intersector.intersectRayPlane(ray,
                    new Plane(Vector3.Z, Vector3.X),
                    intersection);

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
            } else {
                camera.rotateAround(camera.position, Vector3.Z, (screenX - lastDragScreenPos.x) * DEGREES_PER_PIXEL);
                camera.rotateAround(camera.position, camera.direction.cpy().rotate(camera.up, -90), (screenY - lastDragScreenPos.y) * DEGREES_PER_PIXEL);
                lastDragScreenPos.set(screenX, screenY);
            }
        } else {

        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //let hud handle this if event occurred on top of menu
        if (screenX >= hud.getMenuScreenPosition().x) {
            cursor.setColor(0, 0, 0, 0);
        } else {
            cursor.setColor(1, 1, 1, 1);
            moveCursorTo(new Vector3(screenX, screenY, 0));
        }

        moveCursorTo(new Vector3(screenX, screenY, 0));
        return true;
    }

    private void moveCursorTo(Vector3 screenCoords) {
        Vector3 intersection = new Vector3();
        Ray ray = camera.getPickRay(screenCoords.x, screenCoords.y);

        Intersector.intersectRayPlane(ray,
                new Plane(Vector3.Z, Vector3.X),
                intersection);

        // round to texels
        intersection.x = Utils.round(intersection.x, 1f / 16f);
        if (cursor.getTextureRegion().getRegionWidth() % 2 == 1) {
            intersection.x -= 0.5f / 16f;
        }
        intersection.y = Utils.round(intersection.y, 1f / 16f);

        cursor.setPosition(intersection);
        cursor.translate(0, 0, cursor.getHeight() / 2);
    }

    @Override
    public boolean scrolled(int amount) {
        //let hud handle this if event occurred on top of menu
        if (Gdx.input.getX() >= hud.getMenuScreenPosition().x) {
            return false;
        }

        camera.position.add(camera.direction.cpy().nor().scl(-SCROLL_RATIO * amount));//TODO FIX ztratu focusu pri kliknuti na pane
        return true;
    }
}

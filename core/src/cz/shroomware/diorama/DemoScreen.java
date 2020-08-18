package cz.shroomware.diorama;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.ui.Hud;

public class DemoScreen implements Screen, InputProcessor {
    MinimalisticDecalBatch batch;
    PerspectiveCamera camera;
    TextureAtlas atlas;
    TextureRegion cursorRegion;
    TextureRegion floorRegion;
    TextureRegion shadowRegion;
    Vector2 lastDragScreenPos = new Vector2();
    Array<Decal> decals = new Array<Decal>();
    Decal cursor;
    Hud hud;
    InputMultiplexer inputMultiplexer;

    DemoScreen(DioramaGame game) {
        initCamera();
        atlas = game.getAtlas();
        hud = new Hud(game, atlas) {
            @Override
            public void onSelectedItemRegion(TextureAtlas.AtlasRegion region) {
                updateCursor(region);
            }
        };
        cursorRegion = atlas.findRegion("tree");
        floorRegion = atlas.findRegion("floor");
        shadowRegion = atlas.findRegion("shadow");
        batch = new MinimalisticDecalBatch();
        int size = 100;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Decal decal = Decal.newDecal(1, 1, floorRegion, false);
                decal.setPosition(x, y, 0);
                decals.add(decal);
            }
        }

        cursor = Decal.newDecal(1, 1, cursorRegion, true);
        cursor.rotateX(90);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud);
        inputMultiplexer.addProcessor(this);
    }

    private void initCamera() {
        float height = 20;
        double ratio = (double) Gdx.graphics.getWidth() / (double) Gdx.graphics.getHeight();
        float width = (float) (height * ratio);
        camera = new PerspectiveCamera(50, width, height);
        camera.position.set(7, -2, 5);
        camera.lookAt(7, 4, 0);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        for (Decal decal : decals) {
            batch.add(decal);
        }
        camera.update();

        if (hud.hasSelectedRegion()) {
            batch.add(cursor);
        }

        batch.render(camera);

        hud.act();
        hud.draw();
    }

    private void placeObject(Decal source) {
        Decal decal = Decal.newDecal(source.getTextureRegion(), true);
        decal.setPosition(source.getPosition());
        decal.setRotation(source.getRotation());
        decal.setWidth(source.getWidth());
        decal.setHeight(source.getHeight());
        decals.add(decal);

        Decal shadowDecal = Decal.newDecal(shadowRegion, true);
        shadowDecal.setPosition(source.getPosition());
        shadowDecal.setZ(0.01f);
        shadowDecal.setWidth(2);
        shadowDecal.setHeight(2);

        decals.add(shadowDecal);
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

    Vector3 worldPos = new Vector3();
    float degreesPerPixel = 0.4f;
    float translationLimit = 12;

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        moveCursorTo(new Vector3(screenX, screenY, 0));

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
                if(transform.len()>20){//TODO: zkontrolovat
                    transform.nor().scl(20);
                }
                camera.translate(transform);
                cameraLastDragWorldPos = intersection.add(transform);
                cameraLastDragWorldPos.z = 0;
            } else {
                camera.rotateAround(camera.position, Vector3.Z, (screenX - lastDragScreenPos.x) * degreesPerPixel);
                camera.rotateAround(camera.position, camera.direction.cpy().rotate(camera.up, -90), (screenY - lastDragScreenPos.y) * degreesPerPixel);
                lastDragScreenPos.set(screenX, screenY);
            }
        } else {

        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        moveCursorTo(new Vector3(screenX, screenY, 0));
        return false;
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
        return false;
    }
}

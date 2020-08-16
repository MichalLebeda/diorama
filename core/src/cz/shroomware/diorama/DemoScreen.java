package cz.shroomware.diorama;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.ui.Hud;

public class DemoScreen implements Screen, InputProcessor {
    DecalBatch batch;
    PerspectiveCamera camera;
    TextureAtlas atlas;
    TextureRegion cursorRegion;
    TextureRegion floorRegion;
    Array<Decal> decals = new Array<Decal>();
    Decal cursor;
    Hud hud;
    InputMultiplexer inputMultiplexer;

    DemoScreen(DioramaGame game) {
        initCamera();
        atlas = game.getAtlas();
        hud = new Hud(game, atlas);
        cursorRegion = atlas.findRegion("tree");
        floorRegion = atlas.findRegion("floor");
        batch = new DecalBatch(new CameraGroupStrategy(camera));
        int size = 10;
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
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(hud);
    }

    private void initCamera() {
        float height = 20;
        double ratio = (double) Gdx.graphics.getWidth() / (double) Gdx.graphics.getHeight();
        float width = (float) (height * ratio);
        camera = new PerspectiveCamera(50, width, height);
        camera.position.set(5, -2, 4);
        camera.lookAt(5, 2, 0);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        for (Decal decal : decals) {
            batch.add(decal);
        }
        camera.update();

        batch.add(cursor);

        batch.flush();

        hud.act();
        hud.draw();
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    Vector3 worldPos = new Vector3();

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
//        camera.update();//TODO
        Vector3 intersection = new Vector3();
        Ray ray = camera.getPickRay(screenX, screenY);
        intersection = ray.direction.cpy().add(ray.origin);
        Intersector.intersectRayPlane(camera.getPickRay(screenX, screenY).cpy(),
                new Plane(Vector3.Z, Vector3.X),
                intersection);
//        cursor.setPosition(1,1,33);
        intersection.x = Math.round(intersection.x);
        intersection.y = Math.round(intersection.y);
        cursor.setPosition(intersection);
        cursor.translate(0,0,cursor.getHeight()/2);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

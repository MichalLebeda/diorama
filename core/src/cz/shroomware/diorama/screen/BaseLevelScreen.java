package cz.shroomware.diorama.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.Level;

public class BaseLevelScreen implements Screen, InputProcessor {
    protected DioramaGame game;
    protected Color backgroundColor;
    protected SpriteBatch spriteBatch;
    protected MinimalisticDecalBatch decalBatch;
    protected PerspectiveCamera camera;
    protected Level level;

    BaseLevelScreen(DioramaGame game){
        this.game = game;
        spriteBatch = new SpriteBatch();
        decalBatch = new MinimalisticDecalBatch();
    }

    protected void updateBackgorundColor() {
        // Use dominant floor color as background
        Pixmap pixmap = Utils.extractPixmapFromTextureRegion(level.getGrid().getTileAt(0, 0));
        backgroundColor = Utils.getDominantColor(pixmap);
//        backgroundColor.mul(0.9f);
        pixmap.dispose();
    }

    protected float calculateCameraViewportHeight() {
        return 20;
    }

    protected float calculateCameraViewportWidth() {
        double ratio = (double) Gdx.graphics.getWidth() / (double) Gdx.graphics.getHeight();
        return (float) (calculateCameraViewportHeight() * ratio);
    }

    protected void initCamera(Level level) {
        camera = new PerspectiveCamera(
                50,
                calculateCameraViewportWidth(),
                calculateCameraViewportHeight());
        camera.position.set(level.getSize() / 2.f, -2, 5);
        camera.near = 0.1f;
        camera.far = 300;
        camera.lookAt(level.getSize() / 2.f, 4, 0);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT);
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

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

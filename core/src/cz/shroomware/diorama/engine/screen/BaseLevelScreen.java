package cz.shroomware.diorama.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.Resources;

public abstract class BaseLevelScreen implements Screen, InputProcessor {
    protected ShaderProgram spriteBatchShader;
    protected Level level;
    protected Color backgroundColor;
    protected SpriteBatch spriteBatch;
    protected MinimalisticDecalBatch decalBatch;
    protected PerspectiveCamera camera;
    protected Box2DDebugRenderer dr = new Box2DDebugRenderer();

    protected boolean boxDebug = false;

    protected BaseLevelScreen(Resources resources) {
        spriteBatchShader = resources.getSpriteBatchShader();
        spriteBatch = new SpriteBatch();
        decalBatch = new MinimalisticDecalBatch();
    }

    protected void updateBackgroundColor(Level level) {
        // Use dominant floor color as background
        Pixmap pixmap = Utils.extractPixmapFromTextureRegion(level.getFloor().getTileAtIndex(0, 0));
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
        camera.position.set(level.getWidth() / 2.f, -2, 5);
        camera.near = 0.1f;
        camera.far = 300;
        camera.lookAt(level.getWidth() / 2.f, 4, 0);
    }

    @Override
    public void show() {

    }

    protected abstract void drawWorld(float delta);

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.setShader(spriteBatchShader);
        drawWorld(delta);

        if (boxDebug) {
            dr.render(level.getWorld(), camera.combined);
        }
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
        if (keycode == Input.Keys.O) {
            boxDebug = !boxDebug;
            return true;
        }

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

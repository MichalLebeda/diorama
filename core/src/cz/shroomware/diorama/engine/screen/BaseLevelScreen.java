package cz.shroomware.diorama.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
    protected Color backgroundColor = Color.GRAY.cpy();
    protected SpriteBatch spriteBatch;
    protected MinimalisticDecalBatch decalBatch;
    protected Box2DDebugRenderer dr = new Box2DDebugRenderer();

    protected boolean boxDebug = false;

    protected BaseLevelScreen(Resources resources, Level level) {
        this.level = level;

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

    public Level getLevel() {
        return level;
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
            dr.render(level.getWorld(), level.getCamera().combined);
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

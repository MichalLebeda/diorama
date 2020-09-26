package cz.shroomware.diorama.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.screen.PlayLevelScreen;

public class EngineGame extends Game {
    protected Resources resources;
    protected int lastWindowedWidth;
    protected int lastWindowedHeight;
    Screen lastScreen = null;

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullscreen();
        }
        super.render();
    }

    @Override
    public void create() {
        TextureAtlas objectAtlas = new TextureAtlas(Gdx.files.internal("atlas/auto.atlas"));
        TextureAtlas shadowsAtlas = new TextureAtlas(Gdx.files.internal("atlas/shadows.atlas"));

        FileHandle vertexFileHandle = Gdx.files.internal("shaders/sprite.vert");
        FileHandle fragmentFileHandle = Gdx.files.internal("shaders/sprite.frag");

        ShaderProgram spriteBatchShader = new ShaderProgram(vertexFileHandle, fragmentFileHandle);
        if (!spriteBatchShader.isCompiled()) {
            Gdx.app.error("spriteBatchShader", "compilation failed:\n" + spriteBatchShader.getLog());
        }

        resources = new Resources();
        resources.setObjectAtlas(objectAtlas);
        resources.setShadowAtlas(shadowsAtlas);
        resources.setSpriteBatchShader(spriteBatchShader);
    }

    @Override
    public void dispose() {
        Screen screen = getScreen();
        if (screen != null) {
            screen.hide();
        }
    }

    public void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(lastWindowedWidth, lastWindowedHeight);
        } else {
            lastWindowedWidth = Gdx.graphics.getWidth();
            lastWindowedHeight = Gdx.graphics.getHeight();
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    public Resources getResources() {
        return resources;
    }

    public void openGame(String levelFilename, Prototypes prototypes) {
        setScreen(new PlayLevelScreen(this, prototypes, levelFilename));
    }

    @Override
    public void setScreen(Screen screen) {
        lastScreen = getScreen();
        super.setScreen(screen);
    }

    public void returnToLastScreen() {
        setScreen(lastScreen);
    }
}

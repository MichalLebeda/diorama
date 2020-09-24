package cz.shroomware.diorama;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.screen.EditorScreen;
import cz.shroomware.diorama.screen.LogicEditorScreen;
import cz.shroomware.diorama.screen.PlayScreen;
import cz.shroomware.diorama.screen.ProjectSelectionScreen;

public class DioramaGame extends Game {
    EditorScreen editorScreen;
    ProjectSelectionScreen projectSelectionScreen;
    EditorResources editorResources;

    @Override
    public void create() {
        TextureAtlas objectAtlas = new TextureAtlas(Gdx.files.internal("atlas/auto.atlas"));
        TextureAtlas shadowsAtlas = new TextureAtlas(Gdx.files.internal("atlas/shadows.atlas"));
        TextureAtlas uiAtlas = new TextureAtlas(Gdx.files.internal("atlas/ui.atlas"));

        TextureRegion darkBackground = uiAtlas.findRegion("black");

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"), uiAtlas);
        skin.getFont("default-font").getData().setScale(0.32f);
        skin.getFont("default-font").setUseIntegerPositions(false);

        ShaderProgram.pedantic = false;
        ShaderProgram dfShader = new ShaderProgram(Gdx.files.internal("shaders/font.vert"), Gdx.files.internal("shaders/font.frag"));
        if (!dfShader.isCompiled()) {
            Gdx.app.error("fontShader", "compilation failed:\n" + dfShader.getLog());
        }

        ShaderProgram spriteBatchShader = new ShaderProgram(Gdx.files.internal("shaders/sprite.vert"), Gdx.files.internal("shaders/sprite.frag"));
        if (!spriteBatchShader.isCompiled()) {
            Gdx.app.error("spriteBatchShader", "compilation failed:\n" + dfShader.getLog());
        }

        editorResources = new EditorResources();
        editorResources.setObjectAtlas(objectAtlas);
        editorResources.setShadowAtlas(shadowsAtlas);
        editorResources.setUiAtlas(uiAtlas);
        editorResources.setDarkBackgroundRegion(darkBackground);
        editorResources.setSkin(skin);
        editorResources.setDfShader(dfShader);
        editorResources.setSpriteBatchShader(spriteBatchShader);

        projectSelectionScreen = new ProjectSelectionScreen(this);
        setScreen(projectSelectionScreen);

//        Bullet.init();
    }

    int lastWindowedWidth;
    int lastWindowedHeight;

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullscreen();
        }
        super.render();
    }

    @Override
    public void dispose() {
        getScreen().hide();

        if (editorScreen != null) {
            editorScreen.dispose();
        }

        projectSelectionScreen.dispose();
    }

    public EditorResources getEditorResources() {
        return editorResources;
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

    public void openEditor(String filename) {
        editorScreen = new EditorScreen(this, filename);
        setScreen(editorScreen);
    }

    public void returnToEditor() {
        if (editorScreen != null) {
            setScreen(editorScreen);
        }
    }

    public void openGamePreview(String levelFilename, Prototypes prototypes) {
        setScreen(new PlayScreen(this, prototypes, levelFilename));
    }

    public void openLogicEditor(String levelName, Logic logic) {
        setScreen(new LogicEditorScreen(this, levelName, logic));
    }

    public void openSelection() {
        setScreen(projectSelectionScreen);
    }
}

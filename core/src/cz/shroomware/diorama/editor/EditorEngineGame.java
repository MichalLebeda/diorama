package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import cz.shroomware.diorama.editor.screen.LevelEditorScreen;
import cz.shroomware.diorama.editor.screen.LogicEditorScreen;
import cz.shroomware.diorama.editor.screen.ProjectSelectionScreen;
import cz.shroomware.diorama.engine.EngineGame;
import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.screen.PlayLevelScreen;

public class EditorEngineGame extends EngineGame {
    //TODO don't use editorScreen variable
    LevelEditorScreen editorScreen;
    ProjectSelectionScreen projectSelectionScreen;
    //TODO: ask if ok, shadows base Resources resources
    EditorResources resources;

    @Override
    public void create() {
        super.create();

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

        resources = new EditorResources(super.resources);
        resources.setUiAtlas(uiAtlas);
        resources.setDarkBackgroundRegion(darkBackground);
        resources.setSkin(skin);
        resources.setDfShader(dfShader);

        projectSelectionScreen = new ProjectSelectionScreen(this);
        setScreen(projectSelectionScreen);
    }

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

    public EditorResources getResources() {
        return resources;
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
        editorScreen = new LevelEditorScreen(this, filename);
        setScreen(editorScreen);
    }

    //TODO don't use editorScreen variable
    public void returnToEditor() {
        if (editorScreen != null) {
            setScreen(editorScreen);
        }
    }

    public void openGame(String levelFilename, Prototypes prototypes) {
        setScreen(new PlayLevelScreen(this, prototypes, levelFilename));
    }

    public void openLogicEditor(String levelName, Logic logic) {
        setScreen(new LogicEditorScreen(this, levelName, logic));
    }

    public void openSelection() {
        setScreen(projectSelectionScreen);
    }
}

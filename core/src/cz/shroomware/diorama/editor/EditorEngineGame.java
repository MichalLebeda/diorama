package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import cz.shroomware.diorama.editor.screen.LevelEditorScreen;
import cz.shroomware.diorama.editor.screen.LevelSelectionScreen;
import cz.shroomware.diorama.editor.screen.LogicEditorScreen;
import cz.shroomware.diorama.engine.EngineGame;
import cz.shroomware.diorama.engine.Project;
import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.screen.PlayLevelScreen;

public class EditorEngineGame extends EngineGame {
    //TODO don't use editorScreen variable
    LevelEditorScreen editorScreen;
    LevelSelectionScreen levelSelectionScreen;
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

        Project project = new Project("test_proj");
        levelSelectionScreen = new LevelSelectionScreen(this, project);
        setScreen(levelSelectionScreen);
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

        levelSelectionScreen.dispose();
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

    public void openEditor(Project project, String filename) {
        editorScreen = new LevelEditorScreen(this, project, filename);
        setScreen(editorScreen);
    }

    public void openEditorWithNewLevel(Project project, String filename, int width, int height) {
        editorScreen = new LevelEditorScreen(this, project, filename, width, height);
        setScreen(editorScreen);
    }

    //TODO don't use editorScreen variable
    public void returnToEditor() {
        if (editorScreen != null) {
            setScreen(editorScreen);
        }
    }

    public void openGame(FileHandle fileHandle, Prototypes prototypes) {
        setScreen(new PlayLevelScreen(this, prototypes, fileHandle));
    }

    public void openLogicEditor(FileHandle levelName, Logic logic) {
        setScreen(new LogicEditorScreen(this, levelName, logic));
    }

    public void openSelection() {
        setScreen(levelSelectionScreen);
    }
}

package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import cz.shroomware.diorama.editor.screen.LevelEditorScreen;
import cz.shroomware.diorama.editor.screen.LevelSelectionScreen;
import cz.shroomware.diorama.editor.screen.LogicEditorScreen;
import cz.shroomware.diorama.editor.screen.ProjectSelectionScreen;
import cz.shroomware.diorama.engine.EngineGame;
import cz.shroomware.diorama.engine.Project;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.logic.component.LevelSwitcher;
import cz.shroomware.diorama.engine.screen.TestLevelScreen;

public class EditorEngineGame extends EngineGame {
    //TODO don't use editorScreen variable
    LevelEditorScreen editorScreen;
    LevelSelectionScreen levelSelectionScreen;
    public static final String LAST_PROJECT_FILE = "LAST_DIR";
    //TODO: ask if ok, shadows base Resources resources
    EditorResources resources;
    ProjectSelectionScreen projectSelectionScreen;
    Preferences preferences;
    Project project; //TODO move to parent?

    @Override
    public void create() {
        super.create();
        preferences = Gdx.app.getPreferences("editor");

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

        levelSelectionScreen = new LevelSelectionScreen(this);

        projectSelectionScreen = new ProjectSelectionScreen(this, Gdx.files.external(""));

        if (preferences.contains(LAST_PROJECT_FILE)) {
            Project project = new Project(Gdx.files.external(preferences.getString(LAST_PROJECT_FILE)));
            if (project.getFileHandle().exists()) {
                openLevelSelection(project);
                projectSelectionScreen.setCurrentFileHandle(project.getFileHandle().parent());
                return;
            }
        }

        openProjectSelection();
    }

    public Preferences getEditorPreferences() {
        return preferences;
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
        Level level = new Level(project.getLevelFileHandle(filename), this);
        editorScreen = new LevelEditorScreen(this, level);
        setScreen(editorScreen);
    }

    public void openEditorWithNewLevel(Project project, String filename, int width, int height) {
        Level level = new Level(project.getLevelFileHandle(filename), this, width, height);
        editorScreen = new LevelEditorScreen(this, level);
        setScreen(editorScreen);
    }

    //TODO don't use editorScreen variable
    public void returnToEditor() {
        if (editorScreen != null) {
            setScreen(editorScreen);
        }
    }

    public void openProjectSelection() {
        setScreen(projectSelectionScreen);
    }

    public void openLevelSelection(Project project) {
        this.project = project;
        levelSwitcher = new LevelSwitcher(this, project.getLevels());

        preferences.putString(LAST_PROJECT_FILE, project.getFileHandle().path());
        preferences.flush();

        levelSelectionScreen.setProject(project);
        setScreen(levelSelectionScreen);
    }

    public void openLogicEditor(FileHandle fileHandle, Logic logic) {
        setScreen(new LogicEditorScreen(this, fileHandle, logic));
    }

    public void openSelection() {
        setScreen(levelSelectionScreen);
    }

    public void openLevel(FileHandle fileHandle) {
        setScreen(createTestLevelScreen(fileHandle));
    }

    protected TestLevelScreen createTestLevelScreen(FileHandle fileHandle) {
        Level level = new Level(fileHandle, this);
        TestLevelScreen testLevelScreen = new TestLevelScreen(this, level);
        return testLevelScreen;
    }
}

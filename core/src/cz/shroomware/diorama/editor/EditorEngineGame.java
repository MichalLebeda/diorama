package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import cz.shroomware.diorama.editor.screen.LevelEditorScreen;
import cz.shroomware.diorama.editor.screen.LevelSelectionScreen;
import cz.shroomware.diorama.editor.screen.LogicEditorScreen;
import cz.shroomware.diorama.editor.screen.PortalConnectionScreen;
import cz.shroomware.diorama.editor.screen.ProjectSelectionScreen;
import cz.shroomware.diorama.engine.EngineGame;
import cz.shroomware.diorama.engine.Project;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.MetaLevel;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.portal.MetaPortal;
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
            Project project = new Project(this, Gdx.files.external(preferences.getString(LAST_PROJECT_FILE)));
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
        if (project != null) {
            project.saveConfig();
        }

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

    public void openEditor(MetaLevel metaLevel) {
        if (editorScreen != null) {
            editorScreen.dispose();
        }
        Level level = new Level(metaLevel, this);
        editorScreen = new LevelEditorScreen(this, level);
        setScreen(editorScreen);
    }

    public void openEditorWithNewLevel(String name, int width, int height) {
        if (editorScreen != null) {
            editorScreen.dispose();
        }
        Level level = new Level(project.createNewLevel(name), this, width, height);
        editorScreen = new LevelEditorScreen(this, level);
        setScreen(editorScreen);
    }

    //TODO don't use editorScreen variable
    public void returnToEditor() {
        if (editorScreen != null) {
            setScreen(editorScreen);
        }
    }

    public void back() {
        setScreen(lastScreen);
    }

    public void openProjectSelection() {
        setScreen(projectSelectionScreen);
    }

    public void openLevelSelection(Project project) {
        setProject(project);
//        levelSwitcher = new LevelSwitcher(this, project.getLevels());

        preferences.putString(LAST_PROJECT_FILE, project.getFileHandle().path());
        preferences.flush();

        levelSelectionScreen.setProject(project);
        setScreen(levelSelectionScreen);
    }

    public void openProjectConnections() {
        setScreen(new PortalConnectionScreen(this, project));
    }

    public void openLogicEditor(MetaLevel metaLevel, Logic logic) {
        setScreen(new LogicEditorScreen(this, metaLevel.getName(), logic));
    }

    public void openSelection() {
        setScreen(levelSelectionScreen);
    }

    public void openLevel(MetaPortal metaPortal) {
        Level level = new Level(metaPortal.getParentLevel(), this);
        level.setIgnoredPortal(metaPortal);
        TestLevelScreen testLevelScreen = new TestLevelScreen(this, level, metaPortal.getX(), metaPortal.getY());
        setScreen(testLevelScreen);
    }

    public void openLevel(MetaLevel metaLevel, float x, float y) {
        Level level = new Level(metaLevel, this);
        TestLevelScreen testLevelScreen = new TestLevelScreen(this, level, x, y);
        setScreen(testLevelScreen);
    }
}

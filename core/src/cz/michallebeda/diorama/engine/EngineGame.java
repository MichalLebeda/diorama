package cz.michallebeda.diorama.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import cz.michallebeda.diorama.engine.level.Level;
import cz.michallebeda.diorama.engine.level.MetaLevel;
import cz.michallebeda.diorama.engine.level.Prototypes;
import cz.michallebeda.diorama.engine.level.Resources;
import cz.michallebeda.diorama.engine.level.portal.MetaPortal;
import cz.michallebeda.diorama.engine.screen.LevelScreen;

public class EngineGame extends Game {
    protected cz.michallebeda.diorama.engine.level.Resources resources;
    protected int lastWindowedWidth;
    protected int lastWindowedHeight;
    //    protected LevelSwitcher levelSwitcher = null;
    protected Screen lastScreen = null;
    protected cz.michallebeda.diorama.engine.level.Prototypes gameObjectPrototypes;
    protected cz.michallebeda.diorama.engine.Project project;

    String projectName = null;

    public EngineGame() {

    }

    public EngineGame(String projectName) {
        this.projectName = projectName;
    }


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

        resources = new cz.michallebeda.diorama.engine.level.Resources();
        resources.setObjectAtlas(objectAtlas);
        resources.setShadowAtlas(shadowsAtlas);
        resources.setSpriteBatchShader(spriteBatchShader);
        resources.getSpriteBatchShader();

        gameObjectPrototypes = new cz.michallebeda.diorama.engine.level.Prototypes(resources);

        if (projectName != null) {
            FileHandle levelFileHandle = Gdx.files.internal(projectName);
            cz.michallebeda.diorama.engine.Project project = new cz.michallebeda.diorama.engine.Project(this, levelFileHandle);
            setProject(project);

            if (projectName != null) {
                cz.michallebeda.diorama.engine.level.MetaLevel metaLevel = project.getMetaLevel("level_0");
                openLevel(metaLevel, 32, 0);
            }
        }
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

    @Override
    public void setScreen(Screen screen) {
        if (this.project != null) {
            this.project.saveConfig();
        }

        lastScreen = getScreen();
        super.setScreen(screen);
    }

    public Prototypes getGameObjectPrototypes() {
        return gameObjectPrototypes;
    }

//    public LevelSwitcher getLevelSwitcher() {
//        return levelSwitcher;
//    }

    public void openLevel(MetaPortal metaPortal) {
        cz.michallebeda.diorama.engine.level.Level level = new cz.michallebeda.diorama.engine.level.Level(metaPortal.getParentLevel(), this);
        level.setIgnoredPortal(metaPortal);
        LevelScreen levelScreen = new LevelScreen(this, level, metaPortal.getX(), metaPortal.getY());
        setScreen(levelScreen);
    }

    public void openLevel(MetaLevel metaLevel, float x, float y) {
        cz.michallebeda.diorama.engine.level.Level level = new Level(metaLevel, this);
        LevelScreen levelScreen = new LevelScreen(this, level, x, y);
        setScreen(levelScreen);
    }

    public cz.michallebeda.diorama.engine.Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}

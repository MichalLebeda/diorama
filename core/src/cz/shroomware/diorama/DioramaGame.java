package cz.shroomware.diorama;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.io.InputStream;

import cz.shroomware.diorama.editor.Level;
import cz.shroomware.diorama.editor.ProjectSelectionScreen;
import cz.shroomware.diorama.screen.EditorScreen;
import cz.shroomware.diorama.screen.PlayScreen;

public class DioramaGame extends Game {
    EditorScreen editorScreen;
    ProjectSelectionScreen projectSelectionScreen;
    TextureAtlas atlas;
    TextureAtlas shadowsAtlas;
    TextureAtlas uiAtlas;
    TextureRegion darkBackground;
    Skin skin;
    ShaderProgram dfShader;
    ShaderProgram decalShader;

    @Override
    public void create() {
        atlas = new TextureAtlas(Gdx.files.internal("atlas/auto.atlas"));
        shadowsAtlas = new TextureAtlas(Gdx.files.internal("atlas/shadows.atlas"));
        uiAtlas = new TextureAtlas(Gdx.files.internal("atlas/ui.atlas"));
        darkBackground = uiAtlas.findRegion("black");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), uiAtlas);
        skin.getFont("default-font").getData().setScale(0.32f);
        skin.getFont("default-font").setUseIntegerPositions(false);

        dfShader = new ShaderProgram(Gdx.files.internal("shaders/font.vert"), Gdx.files.internal("shaders/font.frag"));
        if (!dfShader.isCompiled()) {
            Gdx.app.error("fontShader", "compilation failed:\n" + dfShader.getLog());
        }

        decalShader = new ShaderProgram(Gdx.files.internal("shaders/v.glsl"), Gdx.files.internal("shaders/f.glsl"));
        if (!decalShader.isCompiled()) {
            Gdx.app.error("decalShader", "compilation failed:\n" + dfShader.getLog());
        }

        projectSelectionScreen = new ProjectSelectionScreen(this);
        setScreen(projectSelectionScreen);
    }

    int lastWindowedWidth;
    int lastWindowedHeight;

    @Override
    public void render() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.F11)){
            toggleFullscreen();
        }
        super.render();
    }

    public void toggleFullscreen(){
        if(Gdx.graphics.isFullscreen()){
            Gdx.graphics.setWindowedMode(lastWindowedWidth,lastWindowedHeight);
        }else {
            lastWindowedWidth = Gdx.graphics.getWidth();
            lastWindowedHeight = Gdx.graphics.getHeight();
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    public void openEditor(String filename) {
        editorScreen = new EditorScreen(this, filename);
        setScreen(editorScreen);
    }

    public void returnToEditor(){
        if(editorScreen!=null){
            setScreen(editorScreen);
        }
    }

    public void openGamePreview(Level level){
        setScreen(new PlayScreen(this,level));
    }

    public void openSelection() {
        setScreen(projectSelectionScreen);
    }

    public TextureRegion getDarkBackground() {
        return darkBackground;
    }

    public Skin getSkin() {
        return skin;
    }

    public ShaderProgram getDFShader() {
        return dfShader;
    }

    public ShaderProgram getDecalShader() {
        return decalShader;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public TextureAtlas getShadowsAtlas() {
        return shadowsAtlas;
    }

    public TextureAtlas getUiAtlas() {
        return uiAtlas;
    }

    @Override
    public void dispose() {
        getScreen().hide();

        if (editorScreen != null) {
            editorScreen.dispose();
        }

        projectSelectionScreen.dispose();
    }
}

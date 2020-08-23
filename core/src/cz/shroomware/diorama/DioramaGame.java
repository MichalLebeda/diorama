package cz.shroomware.diorama;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import cz.shroomware.diorama.editor.ProjectSelectionScreen;
import cz.shroomware.diorama.screen.EditorScreen;

public class DioramaGame extends Game {
    EditorScreen editorScreen;
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

        dfShader = new ShaderProgram(Gdx.files.internal("shaders/font.vert"), Gdx.files.internal("shaders/font.frag"));
        if (!dfShader.isCompiled()) {
            Gdx.app.error("fontShader", "compilation failed:\n" + dfShader.getLog());
        }

        decalShader = new ShaderProgram(Gdx.files.internal("shaders/v.glsl"), Gdx.files.internal("shaders/f.glsl"));
        if (!decalShader.isCompiled()) {
            Gdx.app.error("decalShader", "compilation failed:\n" + dfShader.getLog());
        }

        setScreen(new ProjectSelectionScreen(this));
    }

    public  void openEditor(String filename){
        editorScreen = new EditorScreen(this,filename);
        setScreen(editorScreen);
    }

    public TextureRegion getDarkBackground(){
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
        if(editorScreen!=null){
            editorScreen.dispose();
        }
    }
}

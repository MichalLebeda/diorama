package cz.shroomware.diorama;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import javax.xml.soap.Text;

public class DioramaGame extends Game {
    DemoScreen demoScreen;
    TextureAtlas atlas;
    TextureAtlas uiAtlas;
    Skin skin;
    ShaderProgram dfShader;

    @Override
    public void create() {
        atlas = new TextureAtlas(Gdx.files.internal("atlas/auto.atlas"));
        uiAtlas = new TextureAtlas(Gdx.files.internal("atlas/ui.atlas"));
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), uiAtlas);

        dfShader = new ShaderProgram(Gdx.files.internal("shaders/font.vert"), Gdx.files.internal("shaders/font.frag"));
        if (!dfShader.isCompiled()) {
            Gdx.app.error("fontShader", "compilation failed:\n" + dfShader.getLog());
        }

        demoScreen = new DemoScreen(this);
        setScreen(demoScreen);
    }

    public Skin getSkin() {
        return skin;
    }

    public ShaderProgram getDFShader() {
        return dfShader;
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void dispose() {
        demoScreen.dispose();
    }
}

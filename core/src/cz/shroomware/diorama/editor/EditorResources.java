package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import cz.shroomware.diorama.engine.level.Resources;

public class EditorResources extends Resources {
    protected TextureAtlas uiAtlas;
    protected Skin skin;
    protected TextureRegion darkBackgroundRegion;
    protected TextureRegion cursorRegion;
    protected ShaderProgram dfShader;

    public EditorResources(Resources resources) {
        objectAtlas = resources.getObjectAtlas();
        shadowAtlas = resources.getShadowAtlas();
        spriteBatchShader = resources.getSpriteBatchShader();
    }

    public TextureAtlas getUiAtlas() {
        return uiAtlas;
    }

    public void setUiAtlas(TextureAtlas uiAtlas) {
        this.uiAtlas = uiAtlas;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public TextureRegion getDarkBackgroundRegion() {
        return darkBackgroundRegion;
    }

    public void setDarkBackgroundRegion(TextureRegion darkBackgroundRegion) {
        this.darkBackgroundRegion = darkBackgroundRegion;
    }

    public ShaderProgram getDfShader() {
        return dfShader;
    }

    public void setDfShader(ShaderProgram dfShader) {
        this.dfShader = dfShader;
    }

    public TextureRegion getCursorRegion() {
        return objectAtlas.findRegion("cursor");
    }
}

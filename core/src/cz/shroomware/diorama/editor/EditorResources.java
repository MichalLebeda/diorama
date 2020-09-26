package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import cz.shroomware.diorama.engine.level.Resources;

public class EditorResources extends Resources {
    protected TextureAtlas uiAtlas;
    protected Skin skin;
    protected TextureRegion darkBackgroundRegion;
    protected TextureRegion cursorRegion;
    protected ShaderProgram dfShader;
    protected Drawable slotDrawable;

    public EditorResources(Resources resources) {
        objectAtlas = resources.getObjectAtlas();
        shadowAtlas = resources.getShadowAtlas();
        spriteBatchShader = resources.getSpriteBatchShader();
    }

    public Drawable getSlotDrawable() {
        return slotDrawable;
    }

    public TextureAtlas getUiAtlas() {
        return uiAtlas;
    }

    public void setUiAtlas(TextureAtlas uiAtlas) {
        this.uiAtlas = uiAtlas;

        slotDrawable = new TextureRegionDrawable(uiAtlas.findRegion("slot"));
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

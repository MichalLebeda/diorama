package cz.michallebeda.diorama.editor;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import cz.michallebeda.diorama.engine.level.Resources;

//TODO: refactor
public class EditorResources extends Resources {
    protected TextureAtlas uiAtlas;
    protected Skin skin;
    protected TextureRegion darkBackgroundRegion;
    protected ShaderProgram dfShader;
    protected Drawable slotDrawable;
    protected BitmapFont font;

    public EditorResources(Resources resources) {
        setObjectAtlas(resources.getObjectAtlas());
        setShadowAtlas(resources.getShadowAtlas());
        setSpriteBatchShader(resources.getSpriteBatchShader());
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
        this.font = skin.getFont("default-font");
    }

    public BitmapFont getFont() {
        return font;
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

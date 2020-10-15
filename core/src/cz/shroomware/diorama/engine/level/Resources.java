package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import cz.shroomware.diorama.engine.ColorUtil;

public class Resources {
    protected TextureAtlas objectAtlas;
    protected TextureAtlas shadowAtlas;
    protected ShaderProgram spriteBatchShader;
    protected ColorUtil colorUtil;

    public TextureAtlas getObjectAtlas() {
        return objectAtlas;
    }

    public void setObjectAtlas(TextureAtlas objectAtlas) {
        this.objectAtlas = objectAtlas;

        if (colorUtil != null) {
            colorUtil.dispose();
        }
        colorUtil = new ColorUtil(objectAtlas);
    }

    public ColorUtil getColorUtil() {
        return colorUtil;
    }

    public TextureAtlas getShadowAtlas() {
        return shadowAtlas;
    }

    public void setShadowAtlas(TextureAtlas shadowAtlas) {
        this.shadowAtlas = shadowAtlas;
    }

    public ShaderProgram getSpriteBatchShader() {
        return spriteBatchShader;
    }

    public void setSpriteBatchShader(ShaderProgram spriteBatchShader) {
        this.spriteBatchShader = spriteBatchShader;
    }
}

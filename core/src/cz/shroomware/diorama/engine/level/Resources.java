package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Resources {
    protected TextureAtlas objectAtlas;
    protected TextureAtlas shadowAtlas;
    ShaderProgram spriteBatchShader;

    public TextureAtlas getObjectAtlas() {
        return objectAtlas;
    }

    public void setObjectAtlas(TextureAtlas objectAtlas) {
        this.objectAtlas = objectAtlas;
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

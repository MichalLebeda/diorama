package cz.michallebeda.diorama.engine;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ObjectShadowPair {
    TextureRegion object, shadow;

    public ObjectShadowPair(TextureRegion object, TextureRegion shadow) {
        this.object = object;
        this.shadow = shadow;
    }

    public TextureRegion getObject() {
        return object;
    }

    public TextureRegion getShadow() {
        return shadow;
    }
}

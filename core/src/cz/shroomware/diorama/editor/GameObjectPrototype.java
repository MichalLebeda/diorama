package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

public class GameObjectPrototype {
    protected TextureAtlas.AtlasRegion objectRegion, shadowRegion;

    public GameObjectPrototype(TextureAtlas.AtlasRegion objectRegion, TextureAtlas.AtlasRegion shadowRegion) {
        this(objectRegion);
        this.shadowRegion = shadowRegion;
    }

    public GameObjectPrototype(TextureAtlas.AtlasRegion objectRegion) {
        this.objectRegion = objectRegion;
    }

    public TextureAtlas.AtlasRegion getObjectRegion() {
        return objectRegion;
    }

    public TextureAtlas.AtlasRegion getShadowRegion() {
        return shadowRegion;
    }

    public GameObject createAt(Vector3 position) {
        return new GameObject(position, this);
    }

    public String getName() {
        return objectRegion.name;
    }
}

package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.Cursor;

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

    public GameObject createAtCursor(Cursor cursor) {
        return new GameObject(cursor, this);
    }

    public GameObject createAt(Vector3 position, Quaternion quaternion) {
        return new GameObject(position, quaternion, this);
    }

    public GameObject createAt(float x, float y, Quaternion quaternion) {
        Vector3 position = new Vector3(x, y, ((float) objectRegion.getRegionHeight()) / 2 / Utils.PIXELS_PER_METER);
        return new GameObject(position, quaternion, this);
    }

    public String getName() {
        return objectRegion.name;
    }
}

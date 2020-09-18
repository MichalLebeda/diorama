package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.object.GameObject;

public abstract class Prototype {

    public Prototype() {

    }

    public abstract TextureRegion getIconRegion();

//    public abstract TextureRegion getObjectRegion();

//    public abstract TextureRegion getShadowRegion();

//    public abstract boolean hasShadow();

    public abstract GameObject createAt(float x, float y, Quaternion quaternion);

    public abstract GameObject createAtCursor(GameObject cursor);

    public abstract String getName();

    public abstract boolean dependenciesFulfilled();

    public abstract boolean isAttached();

    protected Vector3 onFloorCoords(float x, float y, TextureRegion region) {
        return new Vector3(x, y, region.getRegionHeight() / Utils.PIXELS_PER_METER / 2f);
    }
}

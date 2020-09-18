package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;

import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.WallObject;

public class WallPrototype extends Prototype {
    Resources resources;
    TextureRegion region;
    TextureRegion top;

    public WallPrototype(Resources resources) {
        this.resources = resources;
        this.region = resources.getObjectAtlas().findRegion("wall");
        this.top = resources.getObjectAtlas().findRegion("wall_top");
    }

    @Override
    public TextureRegion getIconRegion() {
        if (dependenciesFulfilled()) {
            return region;
        }

        return resources.getObjectAtlas().findRegion("cursor");
    }

    @Override
    public GameObject createAt(float x, float y, Quaternion quaternion) {
        return new WallObject(onFloorCoords(x, y, region), this);
    }

    @Override
    public GameObject createAtCursor(GameObject cursor) {
        return new WallObject(cursor.getPosition(), this);
    }

    @Override
    public String getName() {
        return "wall";
    }

    @Override
    public boolean dependenciesFulfilled() {
        return region != null && top != null;
    }

    @Override
    public boolean isAttached() {
        return true;
    }

    public TextureRegion getFrontRegion() {
        return region;
    }

    public TextureRegion getBackRegion() {
        return region;
    }

    public TextureRegion getLeftRegion() {
        return region;
    }

    public TextureRegion getRightRegion() {
        return region;
    }

    public TextureRegion getTop() {
        return top;
    }
}

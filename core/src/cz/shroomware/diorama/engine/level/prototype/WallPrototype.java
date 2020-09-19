package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;

import java.util.HashMap;

import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.WallObject;

public class WallPrototype extends Prototype {
    Resources resources;
    TextureRegion region;
    TextureRegion regionConnectedLeft;
    TextureRegion regionConnectedRight;
    TextureRegion regionConnectedBoth;
    HashMap<String, TextureRegion> topRegions = new HashMap<>();

    public WallPrototype(Resources resources) {
        this.resources = resources;
        this.region = resources.getObjectAtlas().findRegion("wall");
        this.regionConnectedLeft = resources.getObjectAtlas().findRegion("wall_left");
        this.regionConnectedRight = resources.getObjectAtlas().findRegion("wall_right");
        this.regionConnectedBoth = resources.getObjectAtlas().findRegion("wall_both");

        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    for (int d = 0; d < 2; d++) {
                        String name = (a == 0 ? "o" : "x") +
                                (b == 0 ? "o" : "x") +
                                (c == 0 ? "o" : "x") +
                                (d == 0 ? "o" : "x");

                        TextureRegion region = resources.getObjectAtlas().findRegion("wall_top_" + name);

                        topRegions.put(name, region);
                    }
                }
            }
        }
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
        return region != null &&
                topRegions.size() == 16 &&
                regionConnectedLeft != null &&
                regionConnectedRight != null &&
                regionConnectedBoth != null;
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

    public HashMap<String, TextureRegion> getTop() {
        return topRegions;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public TextureRegion getRegionConnectedLeft() {
        return regionConnectedLeft;
    }

    public TextureRegion getRegionConnectedRight() {
        return regionConnectedRight;
    }

    public TextureRegion getRegionConnectedBoth() {
        return regionConnectedBoth;
    }
}

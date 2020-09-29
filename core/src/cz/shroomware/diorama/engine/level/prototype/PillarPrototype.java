package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.HexRegion;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.Pillar;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class PillarPrototype extends Prototype {
    Resources resources;
    TextureRegion region;
    TextureRegion regionConnectedLeft;
    TextureRegion regionConnectedRight;
    TextureRegion regionConnectedBoth;
    HexRegion topRegions;

    public PillarPrototype(Resources resources) {
        this.resources = resources;
        this.region = resources.getObjectAtlas().findRegion("wall");
        this.regionConnectedLeft = resources.getObjectAtlas().findRegion("wall_left");
        this.regionConnectedRight = resources.getObjectAtlas().findRegion("wall_right");
        this.regionConnectedBoth = resources.getObjectAtlas().findRegion("wall_both");
        topRegions = new HexRegion(resources.getObjectAtlas(), "wall_top_");
    }

    @Override
    public TextureRegion getIconRegion() {
            return region;
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory) {
        return new Pillar(position, this, boxFactory);
    }

    @Override
    public String getName() {
        return "pillar";
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
        return false;
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

    public HexRegion getTop() {
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

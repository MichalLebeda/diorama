package cz.michallebeda.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.engine.HexRegion;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.Resources;
import cz.michallebeda.diorama.engine.level.object.GameObject;
import cz.michallebeda.diorama.engine.level.object.Wall;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

public class WallPrototype extends Prototype {
    Resources resources;
    TextureRegion region;
    TextureRegion regionConnectedLeft;
    TextureRegion regionConnectedRight;
    TextureRegion regionConnectedBoth;
    HexRegion topRegions;
    String name;

    public WallPrototype(Resources resources, String name) {
        this.resources = resources;
        this.name = name;
        this.region = resources.getObjectAtlas().findRegion(name);
        this.regionConnectedLeft = resources.getObjectAtlas().findRegion(name + "_left");
        this.regionConnectedRight = resources.getObjectAtlas().findRegion(name + "_right");
        this.regionConnectedBoth = resources.getObjectAtlas().findRegion(name + "_both");
        topRegions = new HexRegion(resources.getObjectAtlas(), name + "_top_");
    }

    @Override
    public TextureRegion getIconRegion() {
        return region;
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier) {
        return new Wall(position, this, boxFactory, identifier);
    }

    @Override
    public String getName() {
        return name;
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

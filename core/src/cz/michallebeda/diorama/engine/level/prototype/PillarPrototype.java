package cz.michallebeda.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.engine.HexRegion;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.Resources;
import cz.michallebeda.diorama.engine.level.object.GameObject;
import cz.michallebeda.diorama.engine.level.object.Pillar;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

public class PillarPrototype extends Prototype {
    Resources resources;
    TextureRegion region;
    HexRegion topRegions;

    public PillarPrototype(Resources resources) {
        this.resources = resources;
        this.region = resources.getObjectAtlas().findRegion("box");
    }

    @Override
    public TextureRegion getIconRegion() {
            return region;
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier) {
        return new Pillar(position, this, boxFactory, identifier);
    }

    @Override
    public String getName() {
        return "pillar";
    }

    @Override
    public boolean dependenciesFulfilled() {
        return region != null;
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
}

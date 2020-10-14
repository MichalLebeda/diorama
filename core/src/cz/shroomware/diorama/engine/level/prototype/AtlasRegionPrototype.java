package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.AtlasRegionGameObject;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class AtlasRegionPrototype extends Prototype {
    protected TextureRegion objectRegion, shadowRegion;
    protected String name;

    public AtlasRegionPrototype(Resources resources, TextureAtlas.AtlasRegion objectRegion) {
        this.objectRegion = objectRegion;
        shadowRegion = resources.getShadowAtlas().findRegion(objectRegion.name);
        name = objectRegion.name;
    }

    public AtlasRegionPrototype(TextureAtlas.AtlasRegion objectRegion) {
        this.objectRegion = objectRegion;
        name = objectRegion.name;
    }

    public TextureRegion getObjectRegion() {
        return objectRegion;
    }

    public TextureRegion getShadowRegion() {
        return shadowRegion;
    }

    @Override
    public TextureRegion getIconRegion() {
        return objectRegion;
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier) {
        return new AtlasRegionGameObject(position, this, identifier);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAttached() {
        return false;
    }

    @Override
    public boolean dependenciesFulfilled() {
        return true;
    }
}

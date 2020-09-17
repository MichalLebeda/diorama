package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;

import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.SingleRegionGameObject;

public class SingleRegionPrototype extends Prototype {
    protected TextureRegion objectRegion, shadowRegion;
    protected String name;

    public SingleRegionPrototype(Resources resources, TextureAtlas.AtlasRegion objectRegion) {
        this.objectRegion = objectRegion;
        shadowRegion = resources.getShadowAtlas().findRegion(objectRegion.name);
        name = objectRegion.name;
    }

    public SingleRegionPrototype(TextureAtlas.AtlasRegion objectRegion) {
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
    public GameObject createAt(float x, float y, Quaternion quaternion) {
        return new SingleRegionGameObject(onFloorCoords(x, y, objectRegion), quaternion, this);
    }

    @Override
    public GameObject createAtCursor(GameObject cursor) {
        return new SingleRegionGameObject(cursor.getPosition(), cursor.getRotation(), this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean dependenciesFulfilled() {
        return true;
    }
}

package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;

import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.Door;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class DoorPrototype extends Prototype {
    Resources resources;
    TextureRegion doorRegion;
    TextureRegion doorPostRegion;

    public DoorPrototype(Resources resources) {
        this.resources = resources;
        doorRegion = resources.getObjectAtlas().findRegion("door");
        doorPostRegion = resources.getObjectAtlas().findRegion("door_post");
    }


    @Override
    public TextureRegion getIconRegion() {
        return doorRegion != null ? doorRegion : resources.getObjectAtlas().findRegion("cursor");
    }

    @Override
    public GameObject createAt(float x, float y, Quaternion quaternion, BoxFactory boxFactory) {
        return new Door(onFloorCoords(x, y, doorPostRegion), quaternion, this, boxFactory);
    }

    @Override
    public GameObject createAtCursor(GameObject cursor, BoxFactory boxFactory) {
        return new Door(cursor.getPosition(), cursor.getRotation(), this, boxFactory);
    }

    @Override
    public String getName() {
        return "door";
    }

    @Override
    public boolean dependenciesFulfilled() {
        return doorRegion != null && doorPostRegion != null;
    }

    @Override
    public boolean isAttached() {
        return true;
    }

    public TextureRegion getDoorRegion() {
        return doorRegion;
    }

    public TextureRegion getDoorPostRegion() {
        return doorPostRegion;
    }
}

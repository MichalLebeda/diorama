package cz.michallebeda.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.Resources;
import cz.michallebeda.diorama.engine.level.object.Door;
import cz.michallebeda.diorama.engine.level.object.GameObject;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

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
        return doorRegion;
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier) {
        return new Door(position, this, boxFactory, identifier);
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

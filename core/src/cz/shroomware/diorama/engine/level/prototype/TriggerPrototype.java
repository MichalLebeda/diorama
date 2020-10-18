package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.Trigger;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class TriggerPrototype extends Prototype {
    Resources resources;
    TextureRegion regionUp;
    TextureRegion regionDown;

    public TriggerPrototype(Resources resources) {
        this.resources = resources;
        regionUp = resources.getObjectAtlas().findRegion("trigger_up");
        regionDown = resources.getObjectAtlas().findRegion("trigger_down");
    }

    @Override
    public TextureRegion getIconRegion() {
        return regionUp;
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier) {
        return new Trigger(position, this, boxFactory, identifier);
    }

    @Override
    public String getName() {
        return "trigger";
    }

    @Override
    public boolean dependenciesFulfilled() {
        return regionUp != null && regionDown != null;
    }

    @Override
    public boolean isAttached() {
        return true;
    }

    public TextureRegion getUpRegion() {
        return regionUp;
    }

    public TextureRegion getDownRegion() {
        return regionDown;
    }
}

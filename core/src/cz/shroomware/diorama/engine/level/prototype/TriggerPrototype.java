package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.Trigger;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class TriggerPrototype extends Prototype {
    Resources resources;
    TextureRegion region;

    public TriggerPrototype(Resources resources) {
        this.resources = resources;
        region = resources.getObjectAtlas().findRegion("wall_top_oooo");
    }

    @Override
    public TextureRegion getIconRegion() {
        return region;
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory) {
        return new Trigger(position, this, boxFactory);
    }

    @Override
    public String getName() {
        return "trigger";
    }

    @Override
    public boolean dependenciesFulfilled() {
        return region != null;
    }

    @Override
    public boolean isAttached() {
        return true;
    }

    public TextureRegion getRegion() {
        return region;
    }
}

package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
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
        region = resources.getObjectAtlas().findRegion("white");
    }

    @Override
    public TextureRegion getIconRegion() {
        if (dependenciesFulfilled()) {
            return region;
        }

        return resources.getObjectAtlas().findRegion("cursor");
    }

    @Override
    public GameObject createAt(float x, float y, Quaternion quaternion, BoxFactory boxFactory) {
        return new Trigger(new Vector3(x, y, 0.1f), region, this, boxFactory);
    }

    @Override
    public GameObject createAtCursor(GameObject cursor, BoxFactory boxFactory) {
        return new Trigger(cursor.getPosition(), region, this, boxFactory);
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
}

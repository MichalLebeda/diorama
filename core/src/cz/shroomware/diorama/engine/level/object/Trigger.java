package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.level.prototype.TriggerPrototype;

public class Trigger extends GameObject {
    public Trigger(Vector3 position, TextureRegion region, TriggerPrototype prototype) {
        super(position, region, prototype);
    }
}

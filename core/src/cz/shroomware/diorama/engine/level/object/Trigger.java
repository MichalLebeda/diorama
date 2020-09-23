package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.level.prototype.TriggerPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Trigger extends GameObject {
    public Trigger(Vector3 position, TextureRegion region, TriggerPrototype prototype, BoxFactory boxFactory) {
        super(position, region, prototype, boxFactory);
    }

    @Override
    protected Body createBody(BoxFactory boxFactory) {
        Body body = boxFactory.addBoxCenter(decal.getX(), decal.getY(), 1, 1);
        body.getFixtureList().get(0).setSensor(true);
        return body;
    }
}

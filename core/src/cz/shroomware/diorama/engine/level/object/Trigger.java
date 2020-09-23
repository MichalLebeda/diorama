package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.level.prototype.TriggerPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Trigger extends GameObject {
    int contacts = 0;

    public Trigger(Vector3 position, TextureRegion region, TriggerPrototype prototype, BoxFactory boxFactory) {
        super(position, region, prototype);
        decal.setWidth(1);
        decal.setHeight(1);

        attachToBody(createBody(boxFactory));
    }

    protected Body createBody(BoxFactory boxFactory) {
        Body body = boxFactory.addBoxCenter(decal.getX(), decal.getY(), 1, 1);
        body.getFixtureList().get(0).setSensor(true);
        return body;
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        if (contacts > 0) {
            decal.setColor(Color.ORANGE);
        } else {
            decal.setColor(Color.WHITE);
        }
        super.drawDecal(decalBatch, delta);
    }

    public void addContact() {
        contacts++;
    }

    public void removeContact() {
        contacts--;
    }
}

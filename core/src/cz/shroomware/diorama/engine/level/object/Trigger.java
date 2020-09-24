package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.prototype.TriggerPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Trigger extends GameObject {
    int contacts = 0;
    Array<Event> events = new Array<>(Event.class);
    Logic logic;

    public Trigger(Vector3 position, TextureRegion region, TriggerPrototype prototype, BoxFactory boxFactory) {
        super(position, region, prototype);

        attachToBody(createBody(boxFactory));

        events.add(new Event(this, "pressed"));
        events.add(new Event(this, "released"));
    }

    protected Body createBody(BoxFactory boxFactory) {
        Body body = boxFactory.addBoxCenter(decal.getX(), decal.getY(), 1, 1);
        body.getFixtureList().get(0).setSensor(true);
        return body;
    }

    public Array<Event> getEvents() {
        return events;
    }

    @Override
    public Array<Handler> getHandlers() {
        return null;
    }

    @Override
    public void onRegister(Logic logic) {
        this.logic = logic;
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        if (contacts > 0) {
            decal.setZ(0.004f);
        } else {
            decal.setZ(1f / Utils.PIXELS_PER_METER);
        }
        super.drawDecal(decalBatch, delta);
    }

    public void addContact() {
        contacts++;
        if (contacts == 1) {
            logic.sendEvent(events.get(0));
        }
    }

    public void removeContact() {
        contacts--;
        if (contacts == 0) {
            logic.sendEvent(events.get(1));
        }
    }
}

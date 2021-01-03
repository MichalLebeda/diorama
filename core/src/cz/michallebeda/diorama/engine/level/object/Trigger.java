package cz.michallebeda.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.Event;
import cz.michallebeda.diorama.engine.level.logic.component.LogicComponent;
import cz.michallebeda.diorama.engine.level.prototype.TriggerPrototype;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

public class Trigger extends GameObject {
    TextureRegion regionUp;
    TextureRegion regionDown;
    int contacts = 0;
    Event pressedEvent;
    Event releasedEvent;
    boolean hit = false;

    public Trigger(Vector3 position, TriggerPrototype prototype, BoxFactory boxFactory, Identifier identifier) {
        super(position, prototype.getUpRegion(), prototype, identifier);

        regionUp = prototype.getUpRegion();
        regionDown = prototype.getDownRegion();

        decal.setRotationX(0);
        decal.setZ(0.001f);

        attachToBody(createBody(boxFactory));

        logicComponent = new LogicComponent(identifier);
        pressedEvent = new Event("pressed");
        releasedEvent = new Event("released");
        logicComponent.addEvent(pressedEvent);
        logicComponent.addEvent(releasedEvent);
    }

    protected Body createBody(BoxFactory boxFactory) {
        Body body = boxFactory.addBoxCenter(decal.getX(), decal.getY(), 1, 1);
        body.getFixtureList().get(0).setSensor(true);
        return body;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (contacts > 0 || hit) {
            decal.setTextureRegion(regionDown);
            hit = false;
        } else {
            decal.setTextureRegion(regionUp);
        }
    }

    @Override
    public void onContactBegin() {
        super.onContactBegin();

        contacts++;
        if (contacts == 1) {
            if (logicComponent.isRegistered()) {
                logicComponent.getLogic().sendEvent(pressedEvent);
            }
        }
    }

    @Override
    public void onContactEnd() {
        super.onContactEnd();

        contacts--;
        if (contacts == 0) {
            if (logicComponent.isRegistered()) {
                logicComponent.getLogic().sendEvent(releasedEvent);
            }
        }
    }

    @Override
    public void setRotation(Quaternion quaternion) {
        // Trigger has fixed rotation
    }

    @Override
    public boolean canWalk() {
        return true;
    }

    @Override
    public void hit(Vector2 playerPosition) {
        onContactBegin();
        onContactEnd();
        hit = true;
    }
}
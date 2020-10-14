package cz.shroomware.diorama.engine.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import cz.shroomware.diorama.engine.level.object.GameObject;

public class LevelContactListener implements ContactListener {

    private boolean isInContact(Contact contact, Body body) {
        return contact.getFixtureA().getBody() == body || contact.getFixtureB().getBody() == body;
    }

    private <T> boolean isInContact(Contact contact, Class<T> tClass) {
        return tClass.isInstance(contact.getFixtureA().getBody().getUserData()) ||
                tClass.isInstance(contact.getFixtureB().getBody().getUserData());
    }

    private <T> T getFromContact(Contact contact, Class<T> tClass) {
        Body[] bodies = {contact.getFixtureA().getBody(), contact.getFixtureB().getBody()};

        Object attachedObject;
        for (Body body : bodies) {
            attachedObject = body.getUserData();

            if (tClass.isInstance(attachedObject)) {
                return (T) attachedObject;
            }
        }

        return null;
    }

    private Object getSecondFromContact(Contact contact, Object first) {
        Body[] bodies = {contact.getFixtureB().getBody(), contact.getFixtureB().getBody()};

        Object attachedObject;
        for (Body body : bodies) {
            attachedObject = body.getUserData();

            if (attachedObject != null && attachedObject != first) {
                return attachedObject;
            }
        }

        return null;
    }

    @Override
    public void beginContact(Contact contact) {
        if (isInContact(contact, GameObject.class)) {
            GameObject gameObject = getFromContact(contact, GameObject.class);
            gameObject.onContactBegin();
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (isInContact(contact, GameObject.class)) {
            GameObject gameObject = getFromContact(contact, GameObject.class);
            gameObject.onContactEnd();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
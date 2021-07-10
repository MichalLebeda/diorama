package cz.michallebeda.diorama.engine.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import cz.michallebeda.diorama.engine.level.object.GameObject;

public class LevelContactListener implements ContactListener {

    private boolean isInContact(Contact contact, Body body) {
        return contact.getFixtureA().getBody() == body || contact.getFixtureB().getBody() == body;
    }

    private <T> boolean isDataOfInstance(Fixture fixture, Class<T> tClass) {
        return tClass.isInstance(fixture.getBody().getUserData()) ||
                tClass.isInstance(fixture.getBody().getUserData());
    }

    private <T> boolean isInContact(Contact contact, Class<T> tClass) {
        return tClass.isInstance(contact.getFixtureA().getBody().getUserData()) ||
                tClass.isInstance(contact.getFixtureB().getBody().getUserData());
    }

    private <T> T getFromFixture(Fixture fixture, Class<T> tClass) {
        Object attachedObject;
        attachedObject = fixture.getBody().getUserData();

        if (tClass.isInstance(attachedObject)) {
            return (T) attachedObject;
        }

        return null;
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
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        cz.michallebeda.diorama.engine.level.object.GameObject gameObject = getFromFixture(fixtureA, cz.michallebeda.diorama.engine.level.object.GameObject.class);
        if (gameObject != null) {
            gameObject.onContactBegin();
        }
        gameObject = getFromFixture(fixtureB, cz.michallebeda.diorama.engine.level.object.GameObject.class);
        if (gameObject != null) {
            gameObject.onContactBegin();
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        cz.michallebeda.diorama.engine.level.object.GameObject gameObject = getFromFixture(fixtureA, cz.michallebeda.diorama.engine.level.object.GameObject.class);
        if (gameObject != null) {
            gameObject.onContactEnd();
        }
        gameObject = getFromFixture(fixtureB, GameObject.class);
        if (gameObject != null) {
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

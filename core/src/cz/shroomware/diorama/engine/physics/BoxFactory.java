package cz.shroomware.diorama.engine.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class BoxFactory {
    private float defDensity = 9, defFriction = 0.4f, defRestitution = 0.3f;
    private Body body;
    private World world;

    public BoxFactory(World world) {
        this.world = world;
    }

    public void changeDefaults(float density, float friction, float restitution) {
        defDensity = density;
        defFriction = friction;
        defRestitution = restitution;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    protected Body createBody(BodyDef bodyDef) {
        synchronized (world) {
            return world.createBody(bodyDef);
        }
    }

    public Body addDynBox(float x, float y, float width, float height, boolean isSensor) {
        width /= 2;
        height /= 2;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set(x + width, y + width);

        body = createBody(bodyDef);

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = bodyShape;
        fixtureDef.isSensor = isSensor;
        fixtureDef.density = defDensity;
        fixtureDef.friction = defFriction;
        fixtureDef.restitution = defRestitution;

        body.createFixture(fixtureDef);
        bodyShape.dispose();

        return body;
    }

    public Body addDynBoxCenter(float x, float y, float width, float height, boolean isSensor) {
        width /= 2;
        height /= 2;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set(x , y);

        body = createBody(bodyDef);

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = bodyShape;
        fixtureDef.isSensor = isSensor;
        fixtureDef.density = defDensity;
        fixtureDef.friction = defFriction;
        fixtureDef.restitution = defRestitution;

        body.createFixture(fixtureDef);
        bodyShape.dispose();

        return body;
    }

    public Body addDynCircle(float x, float y, float radius, short categoryBits, short maskBits) {

        //Dynamic Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body body = createBody(bodyDef);

        CircleShape bodyShape = new CircleShape();
        bodyShape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = bodyShape;

        fixtureDef.density = defDensity;
        fixtureDef.friction = defFriction;
        fixtureDef.restitution = defRestitution;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;

        body.createFixture(fixtureDef);
        body.setLinearDamping(30);

        bodyShape.dispose();
        return body;
    }

//    public Body addBox(float x, float y, float width, float height, short categoryBits, short maskBits) {
    public Body addBox(float x, float y, float width, float height) {
        width /= 2;
        height /= 2;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;


        bodyDef.position.set(x + width, y + height);

        body = createBody(bodyDef);

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = bodyShape;
//        fixtureDef.filter.categoryBits = categoryBits;
//        fixtureDef.filter.maskBits = maskBits;

        body.createFixture(fixtureDef);
        bodyShape.dispose();

        return body;
    }

    public Body addBoxCenter(float x, float y, float width, float height) {
        width /= 2;
        height /= 2;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;


        bodyDef.position.set(x , y);

        body = createBody(bodyDef);

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = bodyShape;
//        fixtureDef.filter.categoryBits = categoryBits;
//        fixtureDef.filter.maskBits = maskBits;

        body.createFixture(fixtureDef);
        bodyShape.dispose();

        return body;
    }

//    public Body addCircle(float x, float y, float radius, short categoryBits, short maskBits) {
        public Body addCircle(float x, float y, float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = createBody(bodyDef);

        CircleShape bodyShape = new CircleShape();
        bodyShape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = bodyShape;
//        fixtureDef.filter.categoryBits = categoryBits;
//        fixtureDef.filter.maskBits = maskBits;

        body.createFixture(fixtureDef);
        bodyShape.dispose();

        return body;
    }
}

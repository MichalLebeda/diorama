package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.level.prototype.SingleRegionPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Player extends SingleRegionGameObject {

    public Player(Vector3 position, Quaternion quaternion, SingleRegionPrototype prototype, BoxFactory boxFactory) {
        super(position, quaternion, prototype);
        attachToBody(createBody(boxFactory));
    }

    protected Player(Vector3 position, SingleRegionPrototype prototype, BoxFactory boxFactory) {
        super(position, prototype);
        attachToBody(createBody(boxFactory));
    }

    protected Body createBody(BoxFactory boxFactory) {
        Body body = boxFactory.addDynBoxCenter(decal.getX(), decal.getY(), 0.5f, 0.5f, false);
        body.setFixedRotation(true);
        body.setLinearDamping(40);
        return body;
    }

    public void update() {
        Vector2 bodyPos = body.getPosition();
        decal.setX(bodyPos.x);
        decal.setY(bodyPos.y);
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        super.drawDecal(decalBatch, delta);
    }

    public void setVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    public Vector2 getBodyPosition() {
        return body.getPosition();
    }
}

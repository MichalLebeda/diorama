package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.level.prototype.AtlasRegionPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Player extends AtlasRegionGameObject {

    public Player(Vector3 position, AtlasRegionPrototype prototype, BoxFactory boxFactory) {
        super(position, prototype);
        attachToBody(createBody(boxFactory));
    }

    protected Body createBody(BoxFactory boxFactory) {
        Body body = boxFactory.addDynBoxCenter(decal.getX(), decal.getY(), 0.5f, 0.5f, false);
        body.setFixedRotation(true);
        body.setLinearDamping(40);
        return body;
    }

    public void setVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    @Override
    protected void updatePosition(float originX, float originY) {
        super.updatePosition(originX, originY);
    }
}

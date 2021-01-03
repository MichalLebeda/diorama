package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.prototype.AtlasRegionPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Player extends AtlasRegionGameObject {
    PerspectiveCamera camera;

    public Player(Vector3 position, AtlasRegionPrototype prototype, BoxFactory boxFactory, PerspectiveCamera camera) {
        super(position, prototype, new Identifier(Utils.PLAYER_ID));
        this.camera = camera;
        camera.position.set(getPosition().cpy().add(0, 0, Utils.CAMERA_LEVEL));
        camera.lookAt(getPosition().cpy().add(0, 1, Utils.CAMERA_LEVEL));

        attachToBody(createBody(boxFactory));
        createShadowSprite(prototype);

        decal.setZ(decal.getHeight() / 2);
    }

    protected Body createBody(BoxFactory boxFactory) {
        Body body = boxFactory.addDynCircle(decal.getX(), decal.getY(), 0.2f);
        body.setFixedRotation(true);
        body.setLinearDamping(40);
        return body;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        camera.position.set(getPosition().cpy().add(0, 0, Utils.CAMERA_LEVEL));
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch) {
//        super.drawDecal(decalBatch);
    }

    @Override
    public void drawShadow(Batch spriteBatch) {
        super.drawShadow(spriteBatch);
    }

    public void setVelocity(float x, float y) {
        Vector2 vec = new Vector2(x, y);
        Vector2 direction = new Vector2(camera.direction.x, camera.direction.y);
        vec.rotate(-direction.angle(Vector2.Y));
        body.setLinearVelocity(vec);
    }

    @Override
    protected void updatePosition(float originX, float originY) {
        super.updatePosition(originX, originY);
    }
}

package cz.michallebeda.diorama.engine.level.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.michallebeda.diorama.Utils;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.prototype.AtlasRegionPrototype;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

public class Player extends AtlasRegionGameObject {
    protected PerspectiveCamera camera;
    protected float health = 100;

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

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return 100;
    }

    public void subtractHealth(float health) {
        Gdx.app.log("Player", "substracted: " + health);
        this.health -= health;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }
}

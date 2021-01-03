package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class GravityParticle extends Particle {
    float timeOnGround = 0;

    public GravityParticle(Vector3 position, TextureRegion region, Color color) {
        super(position, region, color);
    }

    public GravityParticle(Vector3 position, TextureRegion region) {
        super(position, region);
    }

    @Override
    public void update(float delta) {
        velocity.x /= 1.05f;
        velocity.y /= 1.05f;
        if (velocity.z > 0.1) {
            velocity.z /= 1.2f;
        } else {
            velocity.z -= 0.1f;
        }
        super.update(delta);
        if (decal.getPosition().z - decal.getHeight() / 2 < 0) {
            decal.setZ(decal.getHeight() / 2);
            timeOnGround += delta;
        }
    }

    @Override
    public boolean toRemove() {
        return timeOnGround > 10;
    }
}

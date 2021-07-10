package cz.michallebeda.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class FallingParticle extends Particle {
    protected float timeOnGround = 0;
    protected float elapsedTimeOnGround = 0;

    public FallingParticle(Vector3 position,
                           TextureRegion region,
                           Color color,
                           float fallingVelocity) {
        super(position, region, color);
        setVelocityZ(-fallingVelocity);
    }

    public FallingParticle(Vector3 position,
                           TextureRegion region,
                           float fallingVelocity) {
        super(position, region);
        setVelocityZ(-fallingVelocity);
    }

    public FallingParticle(Vector3 position,
                           TextureRegion region,
                           Color color,
                           float fallingVelocity,
                           float timeOnGround) {
        super(position, region, color);
        this.timeOnGround = timeOnGround;
        setVelocityZ(-fallingVelocity);
    }

    public FallingParticle(Vector3 position,
                           TextureRegion region,
                           float fallingVelocity,
                           float timeOnGround) {
        super(position, region);
        this.timeOnGround = timeOnGround;
        setVelocityZ(-fallingVelocity);
    }

    @Override
    protected void applyVelocity(float delta) {
        if (!isOnGround()) {
            velocity.x = (float) Math.sin(time * 2) / 8f;
//            velocity.y = (float) Math.cos(time * 2) / 8f;
            super.applyVelocity(delta);
        }
    }

    public boolean isOnGround() {
        return decal.getZ() <= decal.getHeight() / 2;
    }

    @Override
    protected void incrementTime(float delta) {
        super.incrementTime(delta);
        if (isOnGround()) {
            elapsedTimeOnGround += delta;
        }
    }

    @Override
    public boolean toRemove() {
        if (timeOnGround == 0) {
            return false;
        }
        return isOnGround() && elapsedTimeOnGround > timeOnGround;
    }
}

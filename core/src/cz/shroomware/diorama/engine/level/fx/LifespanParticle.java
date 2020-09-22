package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class LifespanParticle extends Particle {

    protected float lifespan;

    public LifespanParticle(Vector3 position, TextureRegion region, Color color, float lifespan) {
        super(position, region, color);
        this.lifespan = lifespan;
    }

    public LifespanParticle(Vector3 position, TextureRegion region, float lifespan) {
        super(position, region);
        this.lifespan = lifespan;
    }

    @Override
    public boolean toRemove() {
        return time >= lifespan;
    }
}

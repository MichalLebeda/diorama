package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class Particle {
    Decal decal;

    float lifespan;
    float time = 0;

    Vector3 velocity = new Vector3();

    public Particle(Vector3 position, TextureRegion region, Color color, float lifespan) {
        this(position, region, lifespan);

        decal.setColor(color);
    }

    public Particle(Vector3 position, TextureRegion region, float lifespan) {
        this.lifespan = lifespan;
        velocity.z = -0.4f;

        decal = Decal.newDecal(region, true);
        decal.setPosition(position);
        decal.setWidth(region.getRegionWidth() / PIXELS_PER_METER);
        decal.setHeight(region.getRegionHeight() / PIXELS_PER_METER);
    }

    public boolean lifespanElapsed() {
        return time >= lifespan;
    }

    public void draw(MinimalisticDecalBatch decalBatch, float delta) {
        time += delta;
        decal.translate(velocity.cpy().scl(delta));
        decalBatch.add(decal);
    }

    public void rotateX(float degrees) {
        decal.rotateX(degrees);
    }

    public void rotateY(float degrees) {
        decal.rotateY(degrees);
    }

    public void rotateZ(float degrees) {
        decal.rotateZ(degrees);
    }
}

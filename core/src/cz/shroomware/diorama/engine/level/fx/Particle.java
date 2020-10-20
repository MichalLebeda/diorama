package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.UpdatedDecal;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public abstract class Particle {
    UpdatedDecal decal;

    float time = 0;

    Vector3 velocity = new Vector3();

    public Particle(Vector3 position, TextureRegion region, Color color) {
        this(position, region);

        decal.setColor(color);
    }

    public Particle(Vector3 position, TextureRegion region) {
        decal = UpdatedDecal.newDecal(region, true);
        decal.setPosition(position);
        decal.setWidth(region.getRegionWidth() / PIXELS_PER_METER);
        decal.setHeight(region.getRegionHeight() / PIXELS_PER_METER);
    }

    public void setVelocity(float x, float y, float z) {
        velocity.set(x, y, z);
    }

    public void setVelocityX(float value) {
        velocity.x = value;
    }

    public void setVelocityY(float value) {
        velocity.y = value;
    }

    public void setVelocityZ(float value) {
        velocity.z = value;
    }

    public abstract boolean toRemove();

    public void draw(MinimalisticDecalBatch decalBatch, float delta) {
        incrementTime(delta);
        applyVelocity(delta);
        decalBatch.add(decal);
    }

    protected void incrementTime(float delta) {
        time += delta;
    }

    protected void applyVelocity(float delta) {
        decal.translate(velocity.cpy().scl(delta));
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

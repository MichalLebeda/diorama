package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.Resources;

public class Cloud {
    protected Decal decal;
    protected float time = MathUtils.random(0f, 2f * (float) Math.PI);
    protected float speed = MathUtils.random(0.9f, 4f);
    protected Vector3 origPos;

    ParticleEmitter particleEmitter;

    public Cloud(final Resources resources, Vector3 position) {
        decal = Decal.newDecal(resources.getObjectAtlas().findRegions("cloud").random());
        decal.setPosition(position);
        decal.rotateX(90);
        decal.setWidth(((float) decal.getTextureRegion().getRegionWidth()) / Utils.PIXELS_PER_METER);
        decal.setHeight(((float) decal.getTextureRegion().getRegionHeight()) / Utils.PIXELS_PER_METER);
        origPos = decal.getPosition().cpy();

        particleEmitter = new ParticleEmitter(new Vector3(position.x - decal.getWidth() / 2, position.y, position.z - decal.getHeight() / 3), new
                Vector3(decal.getWidth(), 0, decal.getHeight() / 3f), 4.2f) {
            @Override
            protected Particle createParticle(Vector3 position) {
                FallingParticle particle = new FallingParticle(position,
                        resources.getObjectAtlas().findRegion("white"),
                        Color.GRAY,
                        9f);
                particle.rotateX(90);
                return particle;
            }
        };
        particleEmitter.setParticleLimit(10);
    }

    public void draw(MinimalisticDecalBatch decalBatch, float delta) {
        time += delta;
        decal.setX(origPos.x + (float) Math.sin(time * speed) / 30f);
        decalBatch.add(decal);

        if (Utils.RAIN) {
            particleEmitter.draw(decalBatch, delta);
        }
    }
}

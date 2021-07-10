package cz.michallebeda.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.Utils;
import cz.michallebeda.diorama.engine.CustomDecal;

public class Cloud {
    protected CustomDecal decal;
    protected float time = MathUtils.random(0f, 2f * (float) Math.PI);
    protected float speed = MathUtils.random(0.9f, 4f);
    protected Vector3 origPos;

    cz.michallebeda.diorama.engine.level.fx.BoxParticleEmitter particleEmitter;

    public Cloud(final TextureAtlas atlas, Vector3 position) {
        decal = CustomDecal.newDecal(atlas.findRegions("cloud").random());
        decal.setBillboard(true);
        decal.setPosition(position);
        decal.rotateX(90);
        decal.setWidth(((float) decal.getTextureRegion().getRegionWidth()) / Utils.PIXELS_PER_METER);
        decal.setHeight(((float) decal.getTextureRegion().getRegionHeight()) / Utils.PIXELS_PER_METER);
        origPos = decal.getPosition().cpy();

        particleEmitter = new BoxParticleEmitter(position,
                new Vector3(decal.getWidth(), 0, decal.getHeight() / 3f), 4.2f) {
            @Override
            protected Particle createParticle(Vector3 position) {
                cz.michallebeda.diorama.engine.level.fx.FallingParticle particle = new FallingParticle(position,
                        atlas.findRegion("white"),
                        Color.GRAY,
                        9f);
                particle.rotateX(90);
                return particle;
            }
        };
        particleEmitter.setParticleLimit(10);
    }

    public void update(float delta) {
        time += delta;
        decal.setX(origPos.x + (float) Math.sin(time * speed) / 30f);

        if (Utils.RAIN) {
            particleEmitter.update(delta);
        }
    }

    public void draw(MinimalisticDecalBatch decalBatch) {
        decalBatch.add(decal);

        if (Utils.RAIN) {
            particleEmitter.draw(decalBatch);
        }
    }

    public Vector3 getPosition() {
        return decal.getPosition();
    }
}

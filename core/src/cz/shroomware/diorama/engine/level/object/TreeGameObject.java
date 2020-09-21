package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.level.fx.Particle;
import cz.shroomware.diorama.engine.level.fx.ParticleEmitter;
import cz.shroomware.diorama.engine.level.prototype.TreePrototype;

public class TreeGameObject extends SingleRegionGameObject {
    ParticleEmitter particleEmitter;

    public TreeGameObject(Vector3 position, Quaternion quaternion, final TreePrototype prototype) {
        super(position, quaternion, prototype);

        particleEmitter = createParticleEmitter(position, prototype);
    }

    protected TreeGameObject(Vector3 position, final TreePrototype prototype) {
        super(position, prototype);
        particleEmitter = createParticleEmitter(position, prototype);
    }

    protected ParticleEmitter createParticleEmitter(Vector3 position, final TreePrototype prototype) {
        float depth = 1f;
        return new ParticleEmitter(new Vector3(position.x - getWidth() / 2, position.y - depth / 2, position.z - getHeight() / 3), new
                Vector3(getWidth(), depth, getHeight() / 3f), 0.6f) {
            @Override
            protected Particle createParticle(Vector3 position) {
                Particle particle = new Particle(position, prototype.getLeaveParticle(), prototype.getLeaveParticleColor(), 3f);
                particle.rotateX(90);
                return particle;
            }
        };
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        super.drawDecal(decalBatch, delta);

        particleEmitter.draw(decalBatch, delta);
    }
}

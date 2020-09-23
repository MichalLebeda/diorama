package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.level.fx.FallingParticle;
import cz.shroomware.diorama.engine.level.fx.Particle;
import cz.shroomware.diorama.engine.level.fx.ParticleEmitter;
import cz.shroomware.diorama.engine.level.prototype.TreePrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class TreeGameObject extends SingleRegionGameObject {
    ParticleEmitter particleEmitter;

    public TreeGameObject(Vector3 position, Quaternion quaternion, final TreePrototype prototype, BoxFactory boxFactory) {
        super(position, quaternion, prototype, boxFactory);
        particleEmitter = createParticleEmitter(position, prototype);
    }

    protected TreeGameObject(Vector3 position, final TreePrototype prototype, BoxFactory boxFactory) {
        super(position, prototype, boxFactory);
        particleEmitter = createParticleEmitter(position, prototype);
    }

    @Override
    protected Body createBody(BoxFactory boxFactory) {
        body = boxFactory.addCircle(getPosition().x, getPosition().y, 0.4f);
        return body;
//        body.setUserData(this);
    }

    protected ParticleEmitter createParticleEmitter(Vector3 position, final TreePrototype prototype) {
        float depth = 1f;
        return new ParticleEmitter(new Vector3(position.x - getWidth() / 2, position.y - depth / 2, position.z - getHeight() / 3), new
                Vector3(getWidth(), depth, getHeight() / 3f), 0.2f) {
            @Override
            protected Particle createParticle(Vector3 position) {
                FallingParticle particle = new FallingParticle(position,
                        prototype.getLeaveParticle(),
                        prototype.getLeaveParticleColor(),
                        0.3f,
                        10f);
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

package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.level.fx.FallingParticle;
import cz.shroomware.diorama.engine.level.fx.Particle;
import cz.shroomware.diorama.engine.level.fx.ParticleEmitter;
import cz.shroomware.diorama.engine.level.prototype.TreePrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Tree extends GameObject {
    ParticleEmitter particleEmitter;

    public Tree(Vector3 position, TreePrototype prototype, BoxFactory boxFactory) {
        super(position, prototype.getObjectRegion(), prototype);
        particleEmitter = createParticleEmitter(position, prototype);
        attachToBody(createBody(boxFactory));
    }

    protected Body createBody(BoxFactory boxFactory) {
        body = boxFactory.addCircle(getPosition().x, getPosition().y, 0.4f);
        return body;
    }

    protected ParticleEmitter createParticleEmitter(Vector3 position, final TreePrototype prototype) {
        float depth = 1f;
        ParticleEmitter particleEmitter = new ParticleEmitter(new Vector3(position.x - getWidth() / 2, position.y - depth / 2, position.z - getHeight() / 3), new
                Vector3(getWidth(), depth, getHeight() / 3f), 0.2f) {
            @Override
            protected Particle createParticle(Vector3 position) {
                FallingParticle particle = new FallingParticle(position,
                        prototype.getLeaveParticle(),
                        prototype.getLeaveParticleColor(),
                        0.3f);
                particle.rotateX(90);
                return particle;
            }
        };

        particleEmitter.setParticleLimit(4);
        return particleEmitter;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        particleEmitter.update(delta);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        //TODO: set particle emitter position
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch) {
        super.drawDecal(decalBatch);

        particleEmitter.draw(decalBatch);
    }
}

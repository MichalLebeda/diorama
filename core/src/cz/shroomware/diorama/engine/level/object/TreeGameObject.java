package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.level.fx.FallingParticle;
import cz.shroomware.diorama.engine.level.fx.Particle;
import cz.shroomware.diorama.engine.level.fx.ParticleEmitter;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.prototype.TreePrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class TreeGameObject extends SingleRegionGameObject {
    ParticleEmitter particleEmitter;

    public TreeGameObject(Vector3 position, Quaternion quaternion, TreePrototype prototype, BoxFactory boxFactory) {
        super(position, quaternion, prototype);
        particleEmitter = createParticleEmitter(position, prototype);
        attachToBody(createBody(boxFactory));
    }

    protected TreeGameObject(Vector3 position, final TreePrototype prototype, BoxFactory boxFactory) {
        super(position, prototype);
        particleEmitter = createParticleEmitter(position, prototype);
        attachToBody(createBody(boxFactory));
    }

    protected Body createBody(BoxFactory boxFactory) {
        body = boxFactory.addCircle(getPosition().x, getPosition().y, 0.4f);
        return body;
//        body.setUserData(this);
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
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        super.drawDecal(decalBatch, delta);

        particleEmitter.draw(decalBatch, delta);
    }

    @Override
    public Array<Event> getEvents() {
        return null;
    }

    @Override
    public Array<Handler> getHandlers() {
        return null;
    }

    @Override
    public void onRegister(Logic logic) {

    }
}

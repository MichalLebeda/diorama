package cz.michallebeda.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;

import cz.michallebeda.diorama.engine.ColorUtil;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.fx.BoxParticleEmitter;
import cz.michallebeda.diorama.engine.level.fx.FallingParticle;
import cz.michallebeda.diorama.engine.level.fx.Particle;
import cz.michallebeda.diorama.engine.level.prototype.TreePrototype;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

public class Tree extends GameObject {
    BoxParticleEmitter particleEmitter;

    public Tree(Vector3 position, cz.michallebeda.diorama.engine.level.prototype.TreePrototype prototype, BoxFactory boxFactory, Identifier identifier) {
        super(position, prototype.getObjectRegion(), prototype, identifier);
        particleEmitter = createParticleEmitter(position, prototype);
        attachToBody(createBody(boxFactory));
        createShadowSprite(prototype);

        decal.setBillboard(true);
    }

    protected Body createBody(BoxFactory boxFactory) {
        body = boxFactory.addCircle(getPosition().x, getPosition().y, 0.4f);
        return body;
    }

    protected BoxParticleEmitter createParticleEmitter(Vector3 position, final TreePrototype prototype) {
        float depth = 1f;
        BoxParticleEmitter particleEmitter = new BoxParticleEmitter(position,
                new Vector3(getWidth(), depth, getHeight() / 3f), 0.2f) {
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
    protected void updatePosition(float originX, float originY) {
        super.updatePosition(originX, originY);
        particleEmitter.setPosition(originX, originY);
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch) {
        super.drawDecal(decalBatch);

        particleEmitter.draw(decalBatch);
    }

    @Override
    public boolean intersectsWithOpaque(ColorUtil colorUtil, Ray ray, Vector3 boundsIntersection) {
        findIntersectionRayDecalPlane(ray, decal, boundsIntersection);
        return super.intersectsWithOpaque(colorUtil, ray, boundsIntersection);
    }
}

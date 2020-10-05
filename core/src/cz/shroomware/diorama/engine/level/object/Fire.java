package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.level.fx.LifespanParticle;
import cz.shroomware.diorama.engine.level.fx.Particle;
import cz.shroomware.diorama.engine.level.fx.ParticleEmitter;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;
import cz.shroomware.diorama.engine.level.prototype.FirePrototype;

public class Fire extends GameObject {
    protected ParticleEmitter particleEmitter;
    protected Animation<TextureRegion> startAnim;
    protected Animation<TextureRegion> endAnim;
    protected Animation<TextureRegion> fireAnim;
    protected float time = 0;
    protected State state = State.OFF;

    public Fire(Vector3 position, final FirePrototype prototype) {
        super(position, prototype.getStartAnim().getKeyFrame(0), prototype);

        startAnim = prototype.getStartAnim();
        endAnim = prototype.getEndAnim();
        fireAnim = prototype.getFireAnim();

        particleEmitter = new ParticleEmitter(new Vector3(position.x - getWidth() / 2,
                position.y, position.z - getHeight() / 2),
                new Vector3(getWidth(), 0.1f, getHeight()),
                10f) {
            @Override
            protected Particle createParticle(Vector3 position) {
                LifespanParticle particle = new LifespanParticle(position,
                        prototype.getParticleRegion(), Color.ORANGE, 1);
                particle.setVelocityZ(0.6f);
                particle.rotateX(90);

                return particle;
            }
        };

        logicComponent = new LogicComponent(identifier);
        logicComponent.addHandler(new Handler("set_on") {
            @Override
            public void handle() {
                turnOn(true);
            }
        });
        logicComponent.addHandler(new Handler("set_off") {
            @Override
            public void handle() {
                turnOn(false);
            }
        });

        particleEmitter.setEmission(false);
    }

    public void turnOn(boolean on) {
        if (on) {
            switch (state) {
                case OFF:
                    time = 0;
                    state = State.STARTING;
                    break;
                case STARTING:
                    break;
                case ENDING:
                    time = 0;
                    state = State.STARTING;
                    break;
                case FIRE:
            }
        } else {
            switch (state) {
                case OFF:
                    break;
                case STARTING:
                    time = 0;
                    state = State.ENDING;
                    break;
                case ENDING:
                    break;
                case FIRE:
                    time = 0;
                    state = State.ENDING;
                    break;
            }
        }

        particleEmitter.setEmission(on);
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        switch (state) {
            case OFF:
                break;
            case FIRE:
                time += delta;
                decal.setTextureRegion(fireAnim.getKeyFrame(time));
                break;
            case ENDING:
                time += delta;
                if (time >= endAnim.getAnimationDuration()) {
                    time = 0;
                    state = State.OFF;
                    decal.setTextureRegion(startAnim.getKeyFrame(time));
                } else {
                    decal.setTextureRegion(endAnim.getKeyFrame(time));
                }

                break;
            case STARTING:
                time += delta;
                if (time >= startAnim.getAnimationDuration()) {
                    time = 0;
                    state = State.FIRE;
                    decal.setTextureRegion(fireAnim.getKeyFrame(0));
                } else {
                    decal.setTextureRegion(startAnim.getKeyFrame(time));
                }
                break;
        }

        super.drawDecal(decalBatch, delta);

        particleEmitter.draw(decalBatch, delta);
    }

    enum State {OFF, STARTING, ENDING, FIRE}
}
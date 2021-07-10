package cz.michallebeda.diorama.engine.level.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.fx.BoxParticleEmitter;
import cz.michallebeda.diorama.engine.level.fx.LifespanParticle;
import cz.michallebeda.diorama.engine.level.fx.Particle;
import cz.michallebeda.diorama.engine.level.logic.Handler;
import cz.michallebeda.diorama.engine.level.logic.component.LogicComponent;
import cz.michallebeda.diorama.engine.level.prototype.FirePrototype;

public class Fire extends GameObject {
    protected BoxParticleEmitter particleEmitter;
    protected Animation<TextureRegion> startAnim;
    protected Animation<TextureRegion> endAnim;
    protected Animation<TextureRegion> fireAnim;
    protected float time = 0;
    protected State state = State.OFF;

    public Fire(Vector3 position, final FirePrototype prototype, Identifier identifier) {
        super(position, prototype.getStartAnim().getKeyFrame(0), prototype, identifier);

        startAnim = prototype.getStartAnim();
        endAnim = prototype.getEndAnim();
        fireAnim = prototype.getFireAnim();

        particleEmitter = new BoxParticleEmitter(position,
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
    public void update(float delta) {
        super.update(delta);

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

    enum State {OFF, STARTING, ENDING, FIRE}
}

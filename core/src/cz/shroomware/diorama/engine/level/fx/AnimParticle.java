package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class AnimParticle extends Particle {
    protected Animation<TextureRegion> animation;
    protected float timeMultiplier;

    public AnimParticle(Vector3 position, Animation<TextureRegion> animation) {
        super(position, animation.getKeyFrame(0));
        this.animation = animation;
        timeMultiplier = MathUtils.random(0.4f, 1f);
    }

    @Override
    public void update(float delta) {
        super.update(delta * timeMultiplier);
        decal.setTextureRegion(animation.getKeyFrame(time));
    }

    @Override
    public boolean toRemove() {
        return animation.isAnimationFinished(time);
    }
}

package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.ObjectShadowPair;
import cz.shroomware.diorama.engine.RegionAnimation;
import cz.shroomware.diorama.engine.level.prototype.AnimatedPrototype;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class AnimatedGameObject extends GameObject {
    RegionAnimation animation;
    float time;

    public AnimatedGameObject(Vector3 position, AnimatedPrototype prototype) {
        super(position, prototype.getAnimation().first().getObject(), prototype);
        animation = prototype.getAnimation();
        createShadowSprite();
        setRandomAnimOffset();
    }

    private void createShadowSprite() {
        ObjectShadowPair pair = animation.first();
        if (pair.getShadow() != null) {
            shadowSprite = new Sprite(pair.getShadow());
            shadowSprite.setSize(decal.getWidth() * Utils.SHADOW_SCALE, -((float) shadowSprite.getRegionHeight() / (float) shadowSprite.getRegionWidth() * decal.getWidth() * Utils.SHADOW_SCALE));
            shadowSprite.setPosition(decal.getX() - shadowSprite.getWidth() / 2, decal.getY() - shadowSprite.getHeight() - 0.01f / PIXELS_PER_METER);
        }
    }

    public void setRandomAnimOffset() {
        time = MathUtils.random(0f, animation.getAnimationDuration());
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        time += delta;

        decal.setTextureRegion(animation.getKeyFrame(time).getObject());
        TextureRegion shadowRegion = animation.getKeyFrame(time).getShadow();
        if (shadowRegion != null) {
            shadowSprite.setRegion(shadowRegion);
        }
    }

    @Override
    public void drawShadow(Batch spriteBatch) {
        super.drawShadow(spriteBatch);
    }
}

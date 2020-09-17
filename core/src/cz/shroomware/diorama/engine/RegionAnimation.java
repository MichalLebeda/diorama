package cz.shroomware.diorama.engine;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;

public class RegionAnimation extends Animation<ObjectShadowPair> {
    public RegionAnimation(float frameDuration, Array<? extends ObjectShadowPair> keyFrames) {
        super(frameDuration, keyFrames);
    }

    public RegionAnimation(float frameDuration, Array<? extends ObjectShadowPair> keyFrames, PlayMode playMode) {
        super(frameDuration, keyFrames, playMode);
    }

    public RegionAnimation(float frameDuration, ObjectShadowPair... keyFrames) {
        super(frameDuration, keyFrames);
    }

    public ObjectShadowPair first() {
        return getKeyFrames()[0];
    }
}

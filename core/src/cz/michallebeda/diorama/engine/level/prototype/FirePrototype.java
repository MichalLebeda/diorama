package cz.michallebeda.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.Resources;
import cz.michallebeda.diorama.engine.level.object.Fire;
import cz.michallebeda.diorama.engine.level.object.GameObject;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

public class FirePrototype extends Prototype {
    Animation<TextureRegion> startAnim;
    Animation<TextureRegion> endAnim;
    Animation<TextureRegion> fireAnim;
    TextureRegion particleRegion;

    public FirePrototype(Resources resources) {
        startAnim = new Animation<TextureRegion>(0.04f, resources.getObjectAtlas().findRegions("fire_start"), Animation.PlayMode.NORMAL);
        endAnim = new Animation<TextureRegion>(0.1f, resources.getObjectAtlas().findRegions("fire_end"), Animation.PlayMode.NORMAL);
        fireAnim = new Animation<TextureRegion>(0.1f, resources.getObjectAtlas().findRegions("fire_on"), Animation.PlayMode.LOOP);
        particleRegion = resources.getObjectAtlas().findRegion("white");
    }

    public Animation<TextureRegion> getFireAnim() {
        return fireAnim;
    }

    public Animation<TextureRegion> getEndAnim() {
        return endAnim;
    }

    public Animation<TextureRegion> getStartAnim() {
        return startAnim;
    }

    public TextureRegion getParticleRegion() {
        return particleRegion;
    }

    @Override
    public TextureRegion getIconRegion() {
        if (fireAnim.getKeyFrames().length == 0) {
            return null;
        }
        return fireAnim.getKeyFrame(0);
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier) {
        return new Fire(position, this, identifier);
    }

    @Override
    public String getName() {
        return "fire";
    }

    @Override
    public boolean dependenciesFulfilled() {
        return fireAnim != null && startAnim != null && endAnim != null;
    }

    @Override
    public boolean isAttached() {
        return false;
    }
}

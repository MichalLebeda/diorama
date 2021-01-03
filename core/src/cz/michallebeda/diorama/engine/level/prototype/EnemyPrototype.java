package cz.michallebeda.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.RegionAnimation;
import cz.michallebeda.diorama.engine.level.Resources;
import cz.michallebeda.diorama.engine.level.object.Enemy;
import cz.michallebeda.diorama.engine.level.object.GameObject;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

public class EnemyPrototype extends Prototype {
    RegionAnimation animation;
    RegionAnimation shotAnimation;
    String name;

    public EnemyPrototype(Resources resources, String name) {
        Array<TextureAtlas.AtlasRegion> regions = resources.getObjectAtlas().findRegions(name);
        Array<TextureAtlas.AtlasRegion> shotRegions = resources.getObjectAtlas().findRegions(name + "_shot");
        this.animation = RegionAnimation.fromAtlasRegion(0.1f, regions, Animation.PlayMode.LOOP);
        this.shotAnimation = RegionAnimation.fromAtlasRegion(0.2f, shotRegions, Animation.PlayMode.LOOP);
        this.name = name;
    }

    public RegionAnimation getAnimation() {
        return animation;
    }

    public RegionAnimation getShotAnimation() {
        return shotAnimation;
    }

    @Override
    public TextureRegion getIconRegion() {
        if (animation.getKeyFrames().length == 0) {
            return null;
        }
        return animation.first().getObject();
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier) {
        return new Enemy(position, this, identifier, boxFactory);
    }

    @Override
    public String getName() {
        return "enemy_" + name;
    }

    @Override
    public boolean isAttached() {
        return false;
    }

    @Override
    public boolean dependenciesFulfilled() {
        return true;
    }
}

package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.RegionAnimation;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.Enemy;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class EnemyPrototype extends Prototype {
    RegionAnimation regionAnimation;
    String name;

    public EnemyPrototype(Resources resources, String name) {
        Array<TextureAtlas.AtlasRegion> regions = resources.getObjectAtlas().findRegions(name);
        this.regionAnimation = RegionAnimation.fromAtlasRegion(0.1f, regions, Animation.PlayMode.LOOP);
        this.name = name;
    }

    public RegionAnimation getAnimation() {
        return regionAnimation;
    }

    @Override
    public TextureRegion getIconRegion() {
        if (regionAnimation.getKeyFrames().length == 0) {
            return null;
        }
        return regionAnimation.first().getObject();
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

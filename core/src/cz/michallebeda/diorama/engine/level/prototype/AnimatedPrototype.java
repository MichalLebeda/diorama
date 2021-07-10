package cz.michallebeda.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.RegionAnimation;
import cz.michallebeda.diorama.engine.level.object.AnimatedGameObject;
import cz.michallebeda.diorama.engine.level.object.GameObject;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

public class AnimatedPrototype extends Prototype {
    RegionAnimation regionAnimation;
    String name;

    public AnimatedPrototype(RegionAnimation regionAnimation, String name) {
        this.regionAnimation = regionAnimation;
        this.name = name;
    }

    public RegionAnimation getAnimation() {
        return regionAnimation;
    }

    @Override
    public TextureRegion getIconRegion() {
        if(regionAnimation.getKeyFrames().length==0){
            return null;
        }
        return regionAnimation.first().getObject();
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier) {
        return new AnimatedGameObject(position, this, identifier);
    }

    @Override
    public String getName() {
        return "anim_" + name;
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

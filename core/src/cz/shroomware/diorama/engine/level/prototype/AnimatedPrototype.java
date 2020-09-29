package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.RegionAnimation;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.object.AnimatedGameObject;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

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
        return regionAnimation.first().getObject();
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory) {
        return new AnimatedGameObject(position, this) {
            @Override
            public Array<Event> getEvents() {
                return null;
            }

            @Override
            public Array<Handler> getHandlers() {
                return null;
            }

            @Override
            public void onRegister(Logic logic) {

            }
        };
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

package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.Lamp;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class LampPrototype extends Prototype {
    protected TextureRegion onRegion, offRegion;
    Resources resources;

    public LampPrototype(Resources resources) {
        this.resources = resources;
        onRegion = resources.getObjectAtlas().findRegion("lamp_on");
        offRegion = resources.getObjectAtlas().findRegion("lamp_off");
    }

    @Override
    public TextureRegion getIconRegion() {
        return offRegion;
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier) {
        return new Lamp(position, this, identifier);
    }

    @Override
    public String getName() {
        return "lamp";
    }

    @Override
    public boolean dependenciesFulfilled() {
        return onRegion != null & offRegion != null;
    }

    @Override
    public boolean isAttached() {
        return false;
    }

    public TextureRegion getOffRegion() {
        return offRegion;
    }

    public TextureRegion getOnRegion() {
        return onRegion;
    }
}

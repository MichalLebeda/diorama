package cz.michallebeda.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.object.GameObject;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

public abstract class Prototype {

    public Prototype() {

    }

    public abstract TextureRegion getIconRegion();

//    public abstract TextureRegion getObjectRegion();

//    public abstract TextureRegion getShadowRegion();

//    public abstract boolean hasShadow();

    public abstract GameObject createAt(Vector3 position, BoxFactory boxFactory, Identifier identifier);

    public abstract String getName();

    public abstract boolean dependenciesFulfilled();

    public abstract boolean isAttached();
}

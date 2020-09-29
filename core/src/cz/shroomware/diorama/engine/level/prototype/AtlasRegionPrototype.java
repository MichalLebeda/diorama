package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.object.AtlasRegionGameObject;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class AtlasRegionPrototype extends Prototype {
    protected TextureRegion objectRegion, shadowRegion;
    protected String name;

    public AtlasRegionPrototype(Resources resources, TextureAtlas.AtlasRegion objectRegion) {
        this.objectRegion = objectRegion;
        shadowRegion = resources.getShadowAtlas().findRegion(objectRegion.name);
        name = objectRegion.name;
    }

    public AtlasRegionPrototype(TextureAtlas.AtlasRegion objectRegion) {
        this.objectRegion = objectRegion;
        name = objectRegion.name;
    }

    public TextureRegion getObjectRegion() {
        return objectRegion;
    }

    public TextureRegion getShadowRegion() {
        return shadowRegion;
    }

    @Override
    public TextureRegion getIconRegion() {
        return objectRegion;
    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory) {
        return new AtlasRegionGameObject(position, this) {
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
        return name;
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

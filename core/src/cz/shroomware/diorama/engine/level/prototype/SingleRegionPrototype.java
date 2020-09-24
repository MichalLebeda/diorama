package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.SingleRegionGameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class SingleRegionPrototype extends Prototype {
    protected TextureRegion objectRegion, shadowRegion;
    protected String name;

    public SingleRegionPrototype(Resources resources, TextureAtlas.AtlasRegion objectRegion) {
        this.objectRegion = objectRegion;
        shadowRegion = resources.getShadowAtlas().findRegion(objectRegion.name);
        name = objectRegion.name;
    }

    public SingleRegionPrototype(TextureAtlas.AtlasRegion objectRegion) {
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
    public GameObject createAt(float x, float y, Quaternion quaternion, BoxFactory boxFactory) {
        return new SingleRegionGameObject(onFloorCoords(x, y, objectRegion), quaternion, this) {
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
    public GameObject createAtCursor(GameObject cursor, BoxFactory boxFactory) {
        return new SingleRegionGameObject(cursor.getPosition(), cursor.getRotation(), this) {
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

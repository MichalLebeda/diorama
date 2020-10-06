package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.prototype.AtlasRegionPrototype;

public class AtlasRegionGameObject extends GameObject {
    //TODO add shadows everywhere

    public AtlasRegionGameObject(Vector3 position, AtlasRegionPrototype prototype) {
        super(position, prototype.getObjectRegion(), prototype);
        createShadowSprite(prototype);
    }

    protected void createShadowSprite(AtlasRegionPrototype prototype) {
        TextureRegion shadowRegion = prototype.getShadowRegion();
        if (prototype.getShadowRegion() != null) {
            shadowSprite = new Sprite(shadowRegion);
            shadowSprite.setSize(decal.getWidth() * Utils.SHADOW_SCALE, -((float) shadowSprite.getRegionHeight() / (float) shadowSprite.getRegionWidth() * decal.getWidth() * Utils.SHADOW_SCALE));
        }

        positionDirty = true;
    }
}

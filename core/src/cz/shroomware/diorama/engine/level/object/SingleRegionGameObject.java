package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.prototype.SingleRegionPrototype;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class SingleRegionGameObject extends GameObject {

    //TODO nepouzivat prototyp
    public SingleRegionGameObject(Vector3 position, Quaternion quaternion, SingleRegionPrototype prototype) {
        this(position, prototype);
        setRotation(quaternion);
    }

    protected SingleRegionGameObject(Vector3 position, SingleRegionPrototype prototype) {
        super(position, prototype.getObjectRegion(), prototype);

        TextureRegion shadowRegion = prototype.getShadowRegion();
        if (prototype.getShadowRegion() != null) {
            shadowSprite = new Sprite(shadowRegion);
            shadowSprite.setSize(decal.getWidth() * Utils.SHADOW_SCALE, -((float) shadowSprite.getRegionHeight() / (float) shadowSprite.getRegionWidth() * decal.getWidth() * Utils.SHADOW_SCALE));
            shadowSprite.setPosition(decal.getPosition().x - shadowSprite.getWidth() / 2, decal.getPosition().y - shadowSprite.getHeight() - 0.01f / PIXELS_PER_METER);
        }
    }
}
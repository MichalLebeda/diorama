package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.io.Serializable;

import cz.shroomware.diorama.DioramaGame;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class GameObject implements Serializable {
    Decal decal;
    Sprite shadowSprite;

    public GameObject(Vector3 position, TextureRegion decalRegion, TextureRegion shadowRegion) {
        decal = Decal.newDecal(decalRegion, true);
        decal.setPosition(position);
        decal.setRotationX(90);
        decal.setWidth(decalRegion.getRegionWidth() / PIXELS_PER_METER);
        decal.setHeight(decalRegion.getRegionHeight() / PIXELS_PER_METER);

        if (shadowRegion != null) {
            shadowSprite = new Sprite(shadowRegion);
            shadowSprite.setSize(decal.getWidth() * 2, -((float) shadowSprite.getRegionHeight() / (float) shadowSprite.getRegionWidth() * decal.getWidth() * 2));
            shadowSprite.setPosition(decal.getPosition().x - shadowSprite.getWidth() / 2, decal.getPosition().y - shadowSprite.getHeight());
        }
    }

    public void sizeBoundingBox(BoundingBox boundingBox) {
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        min.set(decal.getPosition().x - decal.getWidth() / 2,
                decal.getPosition().y,
                decal.getPosition().z - decal.getHeight() / 2);
        max.set(decal.getPosition().x + decal.getWidth() / 2,
                decal.getPosition().y,
                decal.getPosition().z + decal.getHeight() / 2);
        boundingBox.set(min, max);
    }

    public void drawDecal(MinimalisticDecalBatch decalBatch) {
        decalBatch.add(decal);
    }

    public void drawShadow(Batch spriteBatch) {
        if (shadowSprite != null) {
            shadowSprite.draw(spriteBatch);
        }
    }
}

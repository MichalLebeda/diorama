package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cz.shroomware.diorama.engine.level.object.GameObject;

public class Tile extends Sprite {
    protected TextureRegion region;
    protected GameObject attachedGameObject;
    private int xIndex, yIndex;

    public Tile(int x, int y, TextureRegion region) {
        super(region);
        this.xIndex = x;
        this.yIndex = y;
        this.region = region;
    }

    public void draw(SpriteBatch spriteBatch, float delta) {
        if (attachedGameObject != null) {
            setColor(Color.ORANGE);
        } else {
            setColor(Color.WHITE);
        }
        super.draw(spriteBatch);
    }

    public void detachObject() {
        attachedGameObject = null;
    }

    public void attachObject(GameObject attachedDecal) {
        this.attachedGameObject = attachedDecal;
    }

    public GameObject getAttachedGameObject() {
        return attachedGameObject;
    }

    public boolean hasAttachedObject() {
        return attachedGameObject != null;
    }

    public boolean hasAttachedObjectOfClass(Class tClass) {
        if (!hasAttachedObject()) {
            return false;
        }

        return tClass.isInstance(attachedGameObject);
    }

    @Override
    public void draw(Batch batch, float alphaModulation) {
        Gdx.app.error("Tile", "draw(Batch batch, float alphaModulation): use draw public void draw(DecalBatch decalBatch, SpriteBatch spriteBatch, float delta)");
    }

    @Override
    public void draw(Batch batch) {
        Gdx.app.error("Tile", "draw(Batch batch): use draw public void draw(DecalBatch decalBatch, SpriteBatch spriteBatch, float delta)");
    }

    public int getXIndex() {
        return xIndex;
    }

    public int getYIndex() {
        return yIndex;
    }

    public TextureRegion getRegion() {
        return region;
    }

    @Override
    public void setRegion(TextureRegion region) {
        super.setRegion(region);
        this.region = region;
    }
}

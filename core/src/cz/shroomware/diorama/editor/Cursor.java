package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.object.AtlasRegionGameObject;
import cz.shroomware.diorama.engine.level.prototype.AtlasRegionPrototype;

public class Cursor extends AtlasRegionGameObject {
    Editor editor;
    Level level;
    TextureRegion defaultRegion;
    boolean itemPlacingAllowed = true;
    boolean visible = true;

    public Cursor(Editor editor,
                  EditorResources resources,
                  Level level,
                  TextureAtlas.AtlasRegion defaultRegion) {
        super(Vector3.Zero, new AtlasRegionPrototype(resources, defaultRegion));
        this.editor = editor;
        this.level = level;
        this.defaultRegion = defaultRegion;
    }

    public void draw(SpriteBatch spriteBatch, MinimalisticDecalBatch decalBatch) {
        if (!visible) {
            return;
        }

        if (itemPlacingAllowed) {
            decal.setColor(1, 1, 1, 1);
        } else {
            decal.setColor(1, 1, 1, 0.2f);
        }

        switch (editor.getMode()) {
            case DELETE:
//                if (decal.getTextureRegion() != defaultRegion) {
//                    updateRegion(defaultRegion);
//                }
//                decalBatch.add(decal);
                break;
            case ITEM:
                TextureRegion selectedPrototypeObjectRegion = editor.getPrototypeIcon();
                if (selectedPrototypeObjectRegion == null) {
                    if (decal.getTextureRegion() != defaultRegion) {
                        updateRegion(defaultRegion);
                    }
                } else if (decal.getTextureRegion() != selectedPrototypeObjectRegion) {
                    updateRegion(selectedPrototypeObjectRegion);
                }
                decalBatch.add(decal);
                break;
            case TILE:
                spriteBatch.draw(defaultRegion, (int) getPosition().x, (int) getPosition().y, 1, 1);
                break;
            case TILE_BUCKET:
                spriteBatch.draw(defaultRegion, (int) getPosition().x, (int) getPosition().y, 1, 1);
                break;
        }
    }

    @Override
    public void setPosition(float x, float y) {
        this.setPosition(new Vector2(x, y));
    }

    float zOffset = 0;

    @Override
    public void setPosition(Vector2 worldPos) {
        if ((editor.hasSelectedPrototype() && editor.getCurrentlySelectedPrototype().isAttached())
                || editor.getHardSnap()) {
            worldPos.x = ((int) worldPos.x) + 0.5f;
            worldPos.y = ((int) worldPos.y) + 0.5f;
        } else {
            worldPos = Utils.roundPosition(worldPos, getWidth());
        }

        super.setPosition(worldPos);

        updateZ();

        if (level.isInBounds(worldPos.x, worldPos.y)) {
            allowPlacingItem();
        } else {
            forbidPlacingItem();
        }
    }

    public void incrementZOffset() {
        zOffset += 1 / Utils.PIXELS_PER_METER;
    }

    public void decrementZOffset() {
        zOffset -= 1 / Utils.PIXELS_PER_METER;
        if (zOffset < 0) {
            zOffset = 0;
        }
    }

    private void updateRegion(TextureRegion region) {
        decal.setTextureRegion(region);
        decal.setWidth(region.getRegionWidth() / 16f);
        decal.setHeight(region.getRegionHeight() / 16f);
    }

    public void forbidPlacingItem() {
        itemPlacingAllowed = false;
    }

    public void allowPlacingItem() {
        itemPlacingAllowed = true;
    }

    public boolean isPlacingItemAllowed() {
        return itemPlacingAllowed;
    }

    public void hide() {
        visible = false;
    }

    public void show() {
        visible = true;
    }

    public float getWidth() {
        return decal.getWidth();
    }

    public float getHeight() {
        return decal.getHeight();
    }

    public Quaternion getRotation() {
        return decal.getRotation();
    }

    public Vector3 getPosition() {
        return decal.getPosition();
    }

    public void rotateY(float angle) {
        decal.rotateY(angle);
    }

    public void updateZ() {
        decal.setZ(getHeight() / 2 + zOffset);
    }
}

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
    int floorCursorX;
    int floorCursorY;
    float zOffset = 0;

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

        // Revert offset for floor cursor
        floorCursorX = (int) (getPosition().x - editor.getSnapOffsetX());
        floorCursorY = (int) (getPosition().y - editor.getSnapOffsetY());

        // May be redundant but fixes decal position when user turns snap on,
        // but no mouse movement occurred yet
        // TODO: Proper solution would be to handle onSnapOn event.
        if (editor.getHardSnap()) {
            setPosition(floorCursorX + 0.5f, floorCursorY + 0.5f);
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
                if (editor.getHardSnap()) {
                    drawFloorCursor(spriteBatch);
                }
                decalBatch.add(decal);
                break;
            case TILE:
                drawFloorCursor(spriteBatch);
                break;
            case TILE_BUCKET:
                drawFloorCursor(spriteBatch);
                break;
            case ITEM_MOVE:
                if (editor.getHardSnap() && editor.isMovingObject()) {
                    drawFloorCursor(spriteBatch);
                }
                break;
            case PORTAL:
                drawFloorCursor(spriteBatch);
                break;
        }
    }

    public void drawFloorCursor(SpriteBatch spriteBatch) {
        spriteBatch.draw(defaultRegion, floorCursorX, floorCursorY, 1, 1);
    }

    @Override
    public void setPosition(float x, float y) {
        this.setPosition(new Vector2(x, y));
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

    @Override
    public void setPosition(Vector2 worldPos) {
        if ((editor.hasSelectedPrototype() && editor.getCurrentlySelectedPrototype().isAttached())) {
            worldPos.x = ((int) worldPos.x) + 0.5f;
            worldPos.y = ((int) worldPos.y) + 0.5f;
        } else if (editor.getHardSnap()) {
            worldPos.x = ((int) worldPos.x) + 0.5f + editor.getSnapOffsetX();
            worldPos.y = ((int) worldPos.y) + 0.5f + editor.getSnapOffsetY();
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

    public void updateZ() {
        decal.setZ(getHeight() / 2 + zOffset);
    }
}

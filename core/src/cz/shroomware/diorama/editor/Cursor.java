package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;

public class Cursor {
    Decal decal;
    Editor editor;
    TextureRegion defaultRegion;
    boolean itemPlacingAllowed = true;
    boolean visible = true;
    int worldSize;

    public Cursor(Editor editor, TextureRegion defaultRegion, int worldSize) {
        this.editor = editor;
        this.defaultRegion = defaultRegion;
        this.worldSize = worldSize;
        decal = Decal.newDecal(defaultRegion, true);
        decal.setWidth(defaultRegion.getRegionWidth()/16f);
        decal.setHeight(defaultRegion.getRegionHeight()/16f);
        decal.rotateX(90);
    }

    public void draw(MinimalisticDecalBatch decalBatch) {
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
                if (decal.getTextureRegion() != defaultRegion) {
                    updateRegion(defaultRegion);
                }
                break;
            case ITEM:
                TextureRegion selectedPrototypeObjectRegion = editor.getPrototypeObjectRegion();
                if(selectedPrototypeObjectRegion==null){
                    if (decal.getTextureRegion() != defaultRegion) {
                        updateRegion(defaultRegion);
                    }
                } else if (decal.getTextureRegion() != selectedPrototypeObjectRegion) {
                    updateRegion(selectedPrototypeObjectRegion);
                }
                break;
        }

        decalBatch.add(decal);
    }

    private boolean isInBounds(float x, float y) {
        return (x >= 0 && x <= worldSize && y >= 0 && y <= worldSize);
    }

    public void moveTo(Vector3 worldPos) {
        // round to texels
        worldPos.x = Utils.round(worldPos.x, 1f / 16f);
        if (decal.getTextureRegion().getRegionWidth() % 2 == 1) {
            worldPos.x += 0.5f / 16f;
        }
        worldPos.y = Utils.round(worldPos.y, 1f / 16f);

        if (isInBounds(worldPos.x, worldPos.y)) {
            allowPlacingItem();
        } else {
            forbidPlacingItem();
        }

        decal.setPosition(worldPos);
        decal.translate(0, 0, decal.getHeight() / 2);
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

    public void rotateY(float angle){
        decal.rotateY(angle);
    }
}

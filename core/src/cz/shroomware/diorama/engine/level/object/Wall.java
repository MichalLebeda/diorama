package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.HexRegion;
import cz.shroomware.diorama.engine.level.Floor;
import cz.shroomware.diorama.engine.level.Tile;
import cz.shroomware.diorama.engine.level.prototype.WallPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class Wall extends GameObject {
    Decal leftDecal, rightDecal, frontDecal, backDecal;
    boolean back, right, front, left;
    TextureRegion region;
    TextureRegion regionConnectedLeft;
    TextureRegion regionConnectedRight;
    TextureRegion regionConnectedBoth;
    HexRegion topRegions;

    public Wall(Vector3 position, WallPrototype prototype, BoxFactory boxFactory) {
        super(position, prototype.getTop().get("oooo"), prototype);

        region = prototype.getRegion();
        regionConnectedLeft = prototype.getRegionConnectedLeft();
        regionConnectedRight = prototype.getRegionConnectedRight();
        regionConnectedBoth = prototype.getRegionConnectedBoth();
        topRegions = prototype.getTop();

        decal.setRotationX(0);

        leftDecal = Decal.newDecal(prototype.getLeftRegion(), true);
        leftDecal.rotateX(90);
        leftDecal.rotateY(90);
        leftDecal.setWidth(leftDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        leftDecal.setHeight(leftDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        rightDecal = Decal.newDecal(prototype.getRightRegion(), true);
        rightDecal.rotateX(90);
        rightDecal.rotateY(90);
        rightDecal.setWidth(rightDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        rightDecal.setHeight(rightDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        frontDecal = Decal.newDecal(prototype.getFrontRegion(), true);
        frontDecal.rotateX(90);
        frontDecal.setWidth(frontDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        frontDecal.setHeight(frontDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        backDecal = Decal.newDecal(prototype.getBackRegion(), true);
        backDecal.rotateX(90);
        backDecal.setWidth(backDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        backDecal.setHeight(backDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        attachToBody(createBody(boxFactory));

        positionDirty = true;
    }

    protected Body createBody(BoxFactory boxFactory) {
        return boxFactory.addBoxCenter(decal.getX(), decal.getY(), 1, 1);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            decal.setColor(0.2f, 0.2f, 0.2f, 1);
            leftDecal.setColor(0.2f, 0.2f, 0.2f, 1);
            rightDecal.setColor(0.2f, 0.2f, 0.2f, 1);
            frontDecal.setColor(0.2f, 0.2f, 0.2f, 1);
            backDecal.setColor(0.2f, 0.2f, 0.2f, 1);
        } else {
            decal.setColor(Color.WHITE);
            leftDecal.setColor(Color.WHITE);
            rightDecal.setColor(Color.WHITE);
            frontDecal.setColor(Color.WHITE);
            backDecal.setColor(Color.WHITE);
        }
    }

    @Override
    public float getHeight() {
        return leftDecal.getHeight();
    }

    @Override
    public void sizeBoundingBox(BoundingBox boundingBox) {
        Vector3 min = getPosition().cpy();
        Vector3 max = getPosition().cpy();

        min.x -= 0.5f;
        min.y -= 0.5f;
        min.z = 0;

        max.x += 0.5f;
        max.y += 0.5f;
        max.z = getHeight();

        boundingBox.set(min, max);
    }

    @Override
    public boolean isPixelOpaque(Vector3 intersection) {
        return isPixelOpaque(intersection, decal) ||
                (!left && isPixelOpaque(intersection, leftDecal)) ||
                (!right && isPixelOpaque(intersection, rightDecal)) ||
                (!front && isPixelOpaque(intersection, frontDecal)) ||
                (!back && isPixelOpaque(intersection, backDecal));
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch) {
        super.drawDecal(decalBatch);
        if (!left) {
            decalBatch.add(leftDecal);
        }
        if (!right) {
            decalBatch.add(rightDecal);
        }
        if (!front) {
            decalBatch.add(frontDecal);
        }
        if (!back) {
            decalBatch.add(backDecal);
        }
    }

    @Override
    public void setRotation(Quaternion quaternion) {
        // Wall has fixed rotation
    }

    public void updateSurroundings(Floor floor) {
        Tile tile;

        tile = floor.getTileByOffset(tileAttachedTo, -1, 0);
        if (tile != null && tile.hasAttachedObjectOfClass(Wall.class)) {
            tile = floor.getTileByOffset(tileAttachedTo, 1, 0);
            if (tile != null && tile.hasAttachedObjectOfClass(Wall.class)) {
                frontDecal.setTextureRegion(regionConnectedBoth);
                backDecal.setTextureRegion(regionConnectedBoth);
            } else {
                frontDecal.setTextureRegion(regionConnectedLeft);
                backDecal.setTextureRegion(regionConnectedLeft);
            }
        } else {
            tile = floor.getTileByOffset(tileAttachedTo, 1, 0);
            if (tile != null && tile.hasAttachedObjectOfClass(Wall.class)) {
                frontDecal.setTextureRegion(regionConnectedRight);
                backDecal.setTextureRegion(regionConnectedRight);
            } else {
                frontDecal.setTextureRegion(region);
                backDecal.setTextureRegion(region);
            }
        }

        tile = floor.getTileByOffset(tileAttachedTo, 0, -1);
        if (tile != null && tile.hasAttachedObjectOfClass(Wall.class)) {
            tile = floor.getTileByOffset(tileAttachedTo, 0, 1);
            if (tile != null && tile.hasAttachedObjectOfClass(Wall.class)) {
                leftDecal.setTextureRegion(regionConnectedBoth);
                rightDecal.setTextureRegion(regionConnectedBoth);
            } else {
                leftDecal.setTextureRegion(regionConnectedLeft);
                rightDecal.setTextureRegion(regionConnectedLeft);
            }
        } else {
            tile = floor.getTileByOffset(tileAttachedTo, 0, 1);
            if (tile != null && tile.hasAttachedObjectOfClass(Wall.class)) {
                leftDecal.setTextureRegion(regionConnectedRight);
                rightDecal.setTextureRegion(regionConnectedRight);
            } else {
                leftDecal.setTextureRegion(region);
                rightDecal.setTextureRegion(region);
            }
        }

        tile = floor.getTileByOffset(tileAttachedTo, 0, 1);
        back = (tile != null && tile.hasAttachedObjectOfClass(Wall.class));
        tile = floor.getTileByOffset(tileAttachedTo, 1, 0);
        right = (tile != null && tile.hasAttachedObjectOfClass(Wall.class));
        tile = floor.getTileByOffset(tileAttachedTo, 0, -1);
        front = (tile != null && tile.hasAttachedObjectOfClass(Wall.class));
        tile = floor.getTileByOffset(tileAttachedTo, -1, 0);
        left = (tile != null && tile.hasAttachedObjectOfClass(Wall.class));

        decal.setTextureRegion(topRegions.get(back, right, front, left));
    }

    @Override
    protected void updatePosition(float originX, float originY) {
        decal.setPosition(originX, originY, leftDecal.getHeight());
        leftDecal.setPosition(originX - 0.5f, originY, leftDecal.getHeight() / 2);
        rightDecal.setPosition(originX + 0.5f, originY, rightDecal.getHeight() / 2);
        frontDecal.setPosition(originX, originY - 0.5f, frontDecal.getHeight() / 2);
        backDecal.setPosition(originX, originY + 0.5f, backDecal.getHeight() / 2);
    }
}

package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.HexRegion;
import cz.shroomware.diorama.engine.level.Floor;
import cz.shroomware.diorama.engine.level.Tile;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.prototype.WallPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class WallObject extends GameObject {
    Decal leftDecal, rightDecal, frontDecal, backDecal;
    boolean up, right, down, left;
    TextureRegion region;
    TextureRegion regionConnectedLeft;
    TextureRegion regionConnectedRight;
    TextureRegion regionConnectedBoth;
    HexRegion topRegions;

    public WallObject(Vector3 position, WallPrototype prototype, BoxFactory boxFactory) {
        super(position, prototype.getTop().get("oooo"), prototype);

        region = prototype.getRegion();
        regionConnectedLeft = prototype.getRegionConnectedLeft();
        regionConnectedRight = prototype.getRegionConnectedRight();
        regionConnectedBoth = prototype.getRegionConnectedBoth();
        topRegions = prototype.getTop();

        decal.setRotationX(0);
        decal.setZ(prototype.getLeftRegion().getRegionHeight() / PIXELS_PER_METER);

        leftDecal = Decal.newDecal(prototype.getLeftRegion(), true);
        leftDecal.setPosition(position.cpy().add(-0.5f, 0, 0));
        leftDecal.rotateX(90);
        leftDecal.rotateY(90);
        leftDecal.setWidth(leftDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        leftDecal.setHeight(leftDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        rightDecal = Decal.newDecal(prototype.getRightRegion(), true);
        rightDecal.setPosition(position.cpy().add(0.5f, 0, 0));
        rightDecal.rotateX(90);
        rightDecal.rotateY(90);
        rightDecal.setWidth(rightDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        rightDecal.setHeight(rightDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        frontDecal = Decal.newDecal(prototype.getFrontRegion(), true);
        frontDecal.rotateX(90);
        frontDecal.setPosition(position.cpy().add(0, -0.5f, 0));
        frontDecal.setWidth(frontDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        frontDecal.setHeight(frontDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        backDecal = Decal.newDecal(prototype.getBackRegion(), true);
        backDecal.setPosition(position.cpy().add(0, 0.5f, 0));
        backDecal.rotateX(90);
        backDecal.setWidth(backDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        backDecal.setHeight(backDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        attachToBody(createBody(boxFactory));
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

    public void sizeBoundingBox(BoundingBox boundingBox) {
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        float[] vertices = frontDecal.getVertices();
        min.set(vertices[Decal.X1],
                vertices[Decal.Y1],
                vertices[Decal.Z1]);
        max.set(vertices[Decal.X4],
                vertices[Decal.Y4] + decal.getHeight(),
                vertices[Decal.Z4]);
        boundingBox.set(min, max);
    }

    @Override
    public boolean isPixelOpaque(Vector3 intersection) {
        return isPixelOpaque(intersection, decal) ||
                isPixelOpaque(intersection, leftDecal) ||
                isPixelOpaque(intersection, rightDecal) ||
                isPixelOpaque(intersection, frontDecal) ||
                isPixelOpaque(intersection, backDecal);
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        super.drawDecal(decalBatch, delta);
        if (!left) {
            decalBatch.add(leftDecal);
        }
        if (!right) {
            decalBatch.add(rightDecal);
        }
        if (!down) {
            decalBatch.add(frontDecal);
        }
        if (!up) {
            decalBatch.add(backDecal);
        }
    }

    public void updateSurroundings(Floor floor) {
        Tile tile;

        tile = floor.getTileByOffset(tileAttachedTo, -1, 0);
        if (tile != null && tile.hasAttachedObjectOfClass(WallObject.class)) {
            tile = floor.getTileByOffset(tileAttachedTo, 1, 0);
            if (tile != null && tile.hasAttachedObjectOfClass(WallObject.class)) {
                frontDecal.setTextureRegion(regionConnectedBoth);
                backDecal.setTextureRegion(regionConnectedBoth);
            } else {
                frontDecal.setTextureRegion(regionConnectedLeft);
                backDecal.setTextureRegion(regionConnectedLeft);
            }
        } else {
            tile = floor.getTileByOffset(tileAttachedTo, 1, 0);
            if (tile != null && tile.hasAttachedObjectOfClass(WallObject.class)) {
                frontDecal.setTextureRegion(regionConnectedRight);
                backDecal.setTextureRegion(regionConnectedRight);
            } else {
                frontDecal.setTextureRegion(region);
                backDecal.setTextureRegion(region);
            }
        }

        tile = floor.getTileByOffset(tileAttachedTo, 0, -1);
        if (tile != null && tile.hasAttachedObjectOfClass(WallObject.class)) {
            tile = floor.getTileByOffset(tileAttachedTo, 0, 1);
            if (tile != null && tile.hasAttachedObjectOfClass(WallObject.class)) {
                leftDecal.setTextureRegion(regionConnectedBoth);
                rightDecal.setTextureRegion(regionConnectedBoth);
            } else {
                leftDecal.setTextureRegion(regionConnectedLeft);
                rightDecal.setTextureRegion(regionConnectedLeft);
            }
        } else {
            tile = floor.getTileByOffset(tileAttachedTo, 0, 1);
            if (tile != null && tile.hasAttachedObjectOfClass(WallObject.class)) {
                leftDecal.setTextureRegion(regionConnectedRight);
                rightDecal.setTextureRegion(regionConnectedRight);
            } else {
                leftDecal.setTextureRegion(region);
                rightDecal.setTextureRegion(region);
            }
        }

        tile = floor.getTileByOffset(tileAttachedTo, 0, 1);
        up = (tile != null && tile.hasAttachedObjectOfClass(WallObject.class));
        tile = floor.getTileByOffset(tileAttachedTo, 1, 0);
        right = (tile != null && tile.hasAttachedObjectOfClass(WallObject.class));
        tile = floor.getTileByOffset(tileAttachedTo, 0, -1);
        down = (tile != null && tile.hasAttachedObjectOfClass(WallObject.class));
        tile = floor.getTileByOffset(tileAttachedTo, -1, 0);
        left = (tile != null && tile.hasAttachedObjectOfClass(WallObject.class));

        decal.setTextureRegion(topRegions.get(up, right, down, left));
    }

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
}

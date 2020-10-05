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
import cz.shroomware.diorama.engine.level.prototype.PillarPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class Pillar extends GameObject {
    Decal left, right, front, back;
    TextureRegion region;
    TextureRegion regionConnectedLeft;
    TextureRegion regionConnectedRight;
    TextureRegion regionConnectedBoth;
    HexRegion topRegions;

    public Pillar(Vector3 position, PillarPrototype prototype, BoxFactory boxFactory) {
        super(position, prototype.getTop().get("oooo"), prototype);

        region = prototype.getRegion();
        regionConnectedLeft = prototype.getRegionConnectedLeft();
        regionConnectedRight = prototype.getRegionConnectedRight();
        regionConnectedBoth = prototype.getRegionConnectedBoth();
        topRegions = prototype.getTop();

        decal.setRotationX(0);
        decal.setZ(prototype.getLeftRegion().getRegionHeight() / PIXELS_PER_METER);

        left = Decal.newDecal(prototype.getLeftRegion(), true);
        left.rotateX(90);
        left.rotateY(90);
        left.setWidth(left.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        left.setHeight(left.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        right = Decal.newDecal(prototype.getRightRegion(), true);
        right.rotateX(90);
        right.rotateY(90);
        right.setWidth(right.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        right.setHeight(right.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        front = Decal.newDecal(prototype.getFrontRegion(), true);
        front.rotateX(90);
        front.setWidth(front.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        front.setHeight(front.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        back = Decal.newDecal(prototype.getBackRegion(), true);
        back.rotateX(90);
        back.setWidth(back.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        back.setHeight(back.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        attachToBody(createBody(boxFactory));
        updatePosition();
    }

    protected void updatePosition() {
        decal.setX(body.getPosition().x);
        decal.setY(body.getPosition().y);
        Vector3 position = decal.getPosition();
        left.setPosition(position.cpy().add(-0.5f, 0, -left.getHeight() / 2));
        right.setPosition(position.cpy().add(0.5f, 0, -right.getHeight() / 2));
        front.setPosition(position.cpy().add(0, -0.5f, -front.getHeight() / 2));
        back.setPosition(position.cpy().add(0, 0.5f, -back.getHeight() / 2));
    }

    protected Body createBody(BoxFactory boxFactory) {
        Body body = boxFactory.addDynBoxCenter(decal.getX(), decal.getY(), 1, 1, false);
        body.setFixedRotation(true);
        body.setLinearDamping(40);
        return body;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            decal.setColor(0.2f, 0.2f, 0.2f, 1);
            left.setColor(0.2f, 0.2f, 0.2f, 1);
            right.setColor(0.2f, 0.2f, 0.2f, 1);
            front.setColor(0.2f, 0.2f, 0.2f, 1);
            back.setColor(0.2f, 0.2f, 0.2f, 1);
        } else {
            decal.setColor(Color.WHITE);
            left.setColor(Color.WHITE);
            right.setColor(Color.WHITE);
            front.setColor(Color.WHITE);
            back.setColor(Color.WHITE);
        }
    }

    public void sizeBoundingBox(BoundingBox boundingBox) {
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        float[] vertices = front.getVertices();
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
                isPixelOpaque(intersection, left) ||
                isPixelOpaque(intersection, right) ||
                isPixelOpaque(intersection, front) ||
                isPixelOpaque(intersection, back);
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        updatePosition();
        super.drawDecal(decalBatch, delta);
        decalBatch.add(left);
        decalBatch.add(right);
        decalBatch.add(front);
        decalBatch.add(back);
    }

    public void updateSurroundings(Floor floor) {
        Tile tile;

        tile = floor.getTileByOffset(tileAttachedTo, -1, 0);
        if (tile != null && tile.hasAttachedObjectOfClass(Pillar.class)) {
            tile = floor.getTileByOffset(tileAttachedTo, 1, 0);
            if (tile != null && tile.hasAttachedObjectOfClass(Pillar.class)) {
                front.setTextureRegion(regionConnectedBoth);
                back.setTextureRegion(regionConnectedBoth);
            } else {
                front.setTextureRegion(regionConnectedLeft);
                back.setTextureRegion(regionConnectedLeft);
            }
        } else {
            tile = floor.getTileByOffset(tileAttachedTo, 1, 0);
            if (tile != null && tile.hasAttachedObjectOfClass(Pillar.class)) {
                front.setTextureRegion(regionConnectedRight);
                back.setTextureRegion(regionConnectedRight);
            } else {
                front.setTextureRegion(region);
                back.setTextureRegion(region);
            }
        }

        tile = floor.getTileByOffset(tileAttachedTo, 0, -1);
        if (tile != null && tile.hasAttachedObjectOfClass(Pillar.class)) {
            tile = floor.getTileByOffset(tileAttachedTo, 0, 1);
            if (tile != null && tile.hasAttachedObjectOfClass(Pillar.class)) {
                left.setTextureRegion(regionConnectedBoth);
                right.setTextureRegion(regionConnectedBoth);
            } else {
                left.setTextureRegion(regionConnectedLeft);
                right.setTextureRegion(regionConnectedLeft);
            }
        } else {
            tile = floor.getTileByOffset(tileAttachedTo, 0, 1);
            if (tile != null && tile.hasAttachedObjectOfClass(Pillar.class)) {
                left.setTextureRegion(regionConnectedRight);
                right.setTextureRegion(regionConnectedRight);
            } else {
                left.setTextureRegion(region);
                right.setTextureRegion(region);
            }
        }

        boolean up, right, down, left;
        tile = floor.getTileByOffset(tileAttachedTo, 0, 1);
        up = (tile != null && tile.hasAttachedObjectOfClass(Pillar.class));
        tile = floor.getTileByOffset(tileAttachedTo, 1, 0);
        right = (tile != null && tile.hasAttachedObjectOfClass(Pillar.class));
        tile = floor.getTileByOffset(tileAttachedTo, 0, -1);
        down = (tile != null && tile.hasAttachedObjectOfClass(Pillar.class));
        tile = floor.getTileByOffset(tileAttachedTo, -1, 0);
        left = (tile != null && tile.hasAttachedObjectOfClass(Pillar.class));

        decal.setTextureRegion(topRegions.get(up, right, down, left));
    }

    @Override
    public void setRotation(Quaternion quaternion) {
        // Pillar has fixed rotation
    }
}
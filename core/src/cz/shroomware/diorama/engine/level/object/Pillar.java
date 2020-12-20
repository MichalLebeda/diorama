package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.ColorUtil;
import cz.shroomware.diorama.engine.CustomDecal;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.prototype.PillarPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class Pillar extends GameObject {
    CustomDecal leftDecal, rightDecal, frontDecal, backDecal;
    TextureRegion region;

    public Pillar(Vector3 position, PillarPrototype prototype, BoxFactory boxFactory, Identifier identifier) {
        super(position, prototype.getRegion(), prototype, identifier);

        region = prototype.getRegion();

        decal.setRotationX(0);
        decal.setZ(prototype.getLeftRegion().getRegionHeight() / PIXELS_PER_METER);

        leftDecal = CustomDecal.newDecal(prototype.getLeftRegion(), true);
        leftDecal.rotateX(90);
        leftDecal.rotateY(-90);
        leftDecal.setWidth(leftDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        leftDecal.setHeight(leftDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        rightDecal = CustomDecal.newDecal(prototype.getRightRegion(), true);
        rightDecal.rotateX(90);
        rightDecal.rotateY(90);
        rightDecal.setWidth(rightDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        rightDecal.setHeight(rightDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        frontDecal = CustomDecal.newDecal(prototype.getFrontRegion(), true);
        frontDecal.rotateX(90);
        frontDecal.setWidth(frontDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        frontDecal.setHeight(frontDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        backDecal = CustomDecal.newDecal(prototype.getBackRegion(), true);
        backDecal.rotateX(90);
        backDecal.rotateY(180);
        backDecal.setWidth(backDecal.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        backDecal.setHeight(backDecal.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        attachToBody(createBody(boxFactory));
    }

    @Override
    protected void updatePosition(float originX, float originY) {
        decal.setX(originX);
        decal.setY(originY);
        leftDecal.setPosition(originX - 0.5f, originY, leftDecal.getHeight() / 2);
        rightDecal.setPosition(originX + 0.5f, originY, rightDecal.getHeight() / 2);
        frontDecal.setPosition(originX, originY - 0.5f, frontDecal.getHeight() / 2);
        backDecal.setPosition(originX, originY + 0.5f, backDecal.getHeight() / 2);
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
        min.set(vertices[CustomDecal.X1],
                vertices[CustomDecal.Y1],
                vertices[CustomDecal.Z1]);
        max.set(vertices[CustomDecal.X4],
                vertices[CustomDecal.Y4] + decal.getHeight(),
                vertices[CustomDecal.Z4]);
        boundingBox.set(min, max);
    }

    @Override
    public boolean intersectsWithOpaque(ColorUtil colorUtil, Ray ray, Vector3 boundsIntersection) {
        Vector3 intersection = new Vector3();

        findIntersectionRayDecalPlane(ray, decal, intersection);
        if (isPixelOpaque(colorUtil, intersection, decal)) {
            return true;
        }

        findIntersectionRayDecalPlane(ray, leftDecal, intersection);
        if (isPixelOpaque(colorUtil, intersection, leftDecal)) {
            return true;
        }

        findIntersectionRayDecalPlane(ray, rightDecal, intersection);
        if (isPixelOpaque(colorUtil, intersection, rightDecal)) {
            return true;
        }

        findIntersectionRayDecalPlane(ray, frontDecal, intersection);
        if (isPixelOpaque(colorUtil, intersection, frontDecal)) {
            return true;
        }

        findIntersectionRayDecalPlane(ray, backDecal, intersection);
        return isPixelOpaque(colorUtil, intersection, backDecal);
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch) {
        super.drawDecal(decalBatch);

        decalBatch.add(leftDecal);
        decalBatch.add(rightDecal);
        decalBatch.add(frontDecal);
        decalBatch.add(backDecal);
    }

    @Override
    public void setRotation(Quaternion quaternion) {
        // Pillar has fixed rotation
    }
}

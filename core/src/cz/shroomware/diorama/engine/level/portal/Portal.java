package cz.shroomware.diorama.engine.level.portal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.UpdatedDecal;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Portal extends GameObject {
    private UpdatedDecal leftDecal, rightDecal, frontDecal, backDecal;
    private PortalConnector portalConnector;
    private MetaPortal metaPortal;
    private boolean ignored = false;

    public Portal(PortalConnector portalConnector,
                  MetaPortal metaPortal,
                  BoxFactory boxFactory,
                  Resources resources) {

        super(new Vector3(metaPortal.getPosition(), 0),
                resources.getObjectAtlas().findRegion("cursor"),
                null);

        identifier = metaPortal.identifier;

        this.portalConnector = portalConnector;
        this.metaPortal = metaPortal;

        float x = metaPortal.getPosition().x;
        float y = metaPortal.getPosition().y;

        Body body = boxFactory.addBoxCenter(x, y, metaPortal.getWidth(), metaPortal.getHeight());
        body.getFixtureList().get(0).setSensor(true);

        attachToBody(body);

        decal.setWidth(metaPortal.getWidth());
        decal.setHeight(metaPortal.getHeight());

        decal.setRotationX(0);
        decal.setZ(2);

        leftDecal = UpdatedDecal.newDecal(decal.getTextureRegion(), true);
        leftDecal.rotateX(90);
        leftDecal.rotateY(90);
        leftDecal.setWidth(decal.getHeight());
        leftDecal.setHeight(2);

        rightDecal = UpdatedDecal.newDecal(decal.getTextureRegion(), true);
        rightDecal.rotateX(90);
        rightDecal.rotateY(90);
        rightDecal.setWidth(decal.getHeight());
        rightDecal.setHeight(2);

        frontDecal = UpdatedDecal.newDecal(decal.getTextureRegion(), true);
        frontDecal.rotateX(90);
        frontDecal.setWidth(decal.getWidth());
        frontDecal.setHeight(2);

        backDecal = UpdatedDecal.newDecal(decal.getTextureRegion(), true);
        backDecal.rotateX(90);
        backDecal.setWidth(decal.getWidth());
        backDecal.setHeight(2);

        updatePosition(body.getPosition().x, body.getPosition().y);
    }

    @Override
    protected void updatePosition(float originX, float originY) {
        decal.setX(originX);
        decal.setY(originY);
        leftDecal.setPosition(originX - decal.getWidth() / 2, originY, leftDecal.getHeight() / 2);
        rightDecal.setPosition(originX + decal.getWidth() / 2, originY, rightDecal.getHeight() / 2);
        frontDecal.setPosition(originX, originY - decal.getHeight() / 2, frontDecal.getHeight() / 2);
        backDecal.setPosition(originX, originY + decal.getHeight() / 2, backDecal.getHeight() / 2);
    }

    @Override
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
    public void drawDecal(MinimalisticDecalBatch decalBatch) {
        super.drawDecal(decalBatch);

        decalBatch.add(leftDecal);
        decalBatch.add(rightDecal);
        decalBatch.add(frontDecal);
        decalBatch.add(backDecal);
    }


    @Override
    public void sizeBoundingBox(BoundingBox boundingBox) {
        decal.setColor(Color.WHITE);
        leftDecal.setColor(Color.WHITE);
        rightDecal.setColor(Color.WHITE);
        frontDecal.setColor(Color.WHITE);
        backDecal.setColor(Color.WHITE);

        Vector3 min = getPosition().cpy();
        Vector3 max = getPosition().cpy();

        min.x -= decal.getWidth() / 2;
        min.y -= decal.getHeight() / 2;
        min.z = 0;

        max.x += decal.getWidth() / 2;
        max.y += decal.getHeight() / 2;
        max.z = getHeight();

        boundingBox.set(min, max);
    }

    @Override
    public float getHeight() {
        return 2;
    }

    @Override
    public boolean intersectsWithOpaque(Ray ray, Vector3 boundsIntersection) {
        Vector3 intersection = new Vector3();

        findIntersectionRayDecalPlane(ray, decal, intersection);
        if (isPixelOpaque(intersection, decal)) {
            return true;
        }

        findIntersectionRayDecalPlane(ray, leftDecal, intersection);
        if (isPixelOpaque(intersection, leftDecal)) {
            return true;
        }

        findIntersectionRayDecalPlane(ray, rightDecal, intersection);
        if (isPixelOpaque(intersection, rightDecal)) {
            return true;
        }

        findIntersectionRayDecalPlane(ray, frontDecal, intersection);
        if (isPixelOpaque(intersection, frontDecal)) {
            return true;
        }

        findIntersectionRayDecalPlane(ray, backDecal, intersection);
        if (isPixelOpaque(intersection, backDecal)) {
            return true;
        }

        return false;
    }

    public MetaPortal getMetaPortal() {
        return metaPortal;
    }

    public void setIgnored() {
        ignored = true;
    }

    @Override
    public void onContactBegin() {
        if (ignored) {
            return;
        }

        super.onContactBegin();

        portalConnector.goThrough(metaPortal);
    }

    @Override
    public void onContactEnd() {
        ignored = false;

        super.onContactEnd();
    }

    @Override
    public String getName() {
        return "portal";
    }
}

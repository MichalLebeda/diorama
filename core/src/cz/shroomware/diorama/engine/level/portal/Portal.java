package cz.shroomware.diorama.engine.level.portal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.ColorUtil;
import cz.shroomware.diorama.engine.CustomDecal;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Portal extends GameObject {
    private CustomDecal leftDecal, rightDecal, frontDecal, backDecal;
    private PortalConnector portalConnector;
    private MetaPortal metaPortal;
    private boolean temporalIgnore = false;
    private boolean enabled = true;

    public Portal(final PortalConnector portalConnector,
                  final MetaPortal metaPortal,
                  BoxFactory boxFactory,
                  Resources resources) {

        super(new Vector3(metaPortal.getPosition(), 0),
                resources.getObjectAtlas().findRegion("cursor"),
                null,
                metaPortal.getIdentifier());

        this.portalConnector = portalConnector;
        this.metaPortal = metaPortal;

        logicComponent = new LogicComponent(metaPortal.identifier);
        logicComponent.addHandler(new Handler("go_through") {
            @Override
            public void handle() {
                portalConnector.goThrough(metaPortal);
            }
        });

        logicComponent.addHandler(new Handler("enable") {
            @Override
            public void handle() {
                setEnabled(true);
            }
        });

        logicComponent.addHandler(new Handler("disable") {
            @Override
            public void handle() {
                setEnabled(false);
            }
        });


        float x = metaPortal.getPosition().x;
        float y = metaPortal.getPosition().y;

        Body body = boxFactory.addBoxCenter(x, y, metaPortal.getWidth(), metaPortal.getHeight());
        body.getFixtureList().get(0).setSensor(true);

        attachToBody(body);

        decal.setWidth(metaPortal.getWidth());
        decal.setHeight(metaPortal.getHeight());

        decal.setRotationX(0);
        decal.setZ(2);

        leftDecal = CustomDecal.newDecal(decal.getTextureRegion(), true);
        leftDecal.rotateX(90);
        leftDecal.rotateY(90);
        leftDecal.setWidth(decal.getHeight());
        leftDecal.setHeight(2);

        rightDecal = CustomDecal.newDecal(decal.getTextureRegion(), true);
        rightDecal.rotateX(90);
        rightDecal.rotateY(90);
        rightDecal.setWidth(decal.getHeight());
        rightDecal.setHeight(2);

        frontDecal = CustomDecal.newDecal(decal.getTextureRegion(), true);
        frontDecal.rotateX(90);
        frontDecal.setWidth(decal.getWidth());
        frontDecal.setHeight(2);

        backDecal = CustomDecal.newDecal(decal.getTextureRegion(), true);
        backDecal.rotateX(90);
        backDecal.setWidth(decal.getWidth());
        backDecal.setHeight(2);

        positionDirty = true;
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
        if (isPixelOpaque(colorUtil, intersection, backDecal)) {
            return true;
        }

        return false;
    }

    public MetaPortal getMetaPortal() {
        return metaPortal;
    }

    public void setIgnored() {
        temporalIgnore = true;
    }

    public void setEnabled(boolean forceIgnore) {
        this.enabled = forceIgnore;
    }

    @Override
    public void onContactBegin() {
        if (temporalIgnore) {
            return;
        }

        if (!enabled) {
            return;
        }

        portalConnector.goThrough(metaPortal);
    }

    @Override
    public void onContactEnd() {
        temporalIgnore = false;

        super.onContactEnd();
    }

    @Override
    public String getName() {
        return "portal";
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        metaPortal.setPosition(x, y);
        metaPortal.getParentLevel().getMetaPortals().setDirty();
    }

    @Override
    public void setPosition(Vector2 position) {
        super.setPosition(position);
        metaPortal.setPosition(position);
        metaPortal.getParentLevel().getMetaPortals().setDirty();
    }
}

package cz.michallebeda.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import cz.michallebeda.diorama.engine.ColorUtil;
import cz.michallebeda.diorama.engine.CustomDecal;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.Handler;
import cz.michallebeda.diorama.engine.level.logic.component.LogicComponent;
import cz.michallebeda.diorama.engine.level.prototype.DoorPrototype;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

import static cz.michallebeda.diorama.Utils.PIXELS_PER_METER;

public class Door extends GameObject {
    private static final float ANIM_DURATION = 1;
    private static final float OPEN_RELATIVE_ANGLE = 84;
    protected boolean open = false;
    protected CustomDecal movingPart;
    float targetRelativeAngle;
    float startRelativeAngle;
    float relativeAngle = 0;
    float time = 0;
    boolean animation;
    Vector2 rotVector = new Vector2();
    Vector2 rotOriginVector = new Vector2();

    public Door(Vector3 position,
                DoorPrototype prototype,
                BoxFactory boxFactory,
                Identifier identifier) {
        super(position, prototype.getDoorPostRegion(), prototype, identifier);

        movingPart = CustomDecal.newDecal(prototype.getDoorRegion(), true);
        movingPart.setPosition(position);
        movingPart.setRotation(decal.getRotation());
        movingPart.setWidth(prototype.getDoorRegion().getRegionWidth() / PIXELS_PER_METER);
        movingPart.setHeight(prototype.getDoorRegion().getRegionHeight() / PIXELS_PER_METER);

        attachToBody(boxFactory.addBoxCenter(decal.getX(), decal.getY(), 1, 0.1f));

        logicComponent = new LogicComponent(identifier);
        logicComponent.addHandler(new Handler("open") {
            @Override
            public void handle() {
                // Open door in opposite direction
                open(new Vector3(getPosition().cpy().add(0, -1, 0)));
            }
        });

        logicComponent.addHandler(new Handler("close") {
            @Override
            public void handle() {
                close();
            }
        });
    }

    public void open(Vector3 openedByPos) {
        if (!open) {
            open = true;
            Plane plane = new Plane(getNormalVector(), decal.getPosition());
            Plane.PlaneSide side = plane.testPoint(openedByPos);

            if (side == Plane.PlaneSide.Front) {
                animateToRelativeAngle(-OPEN_RELATIVE_ANGLE);
            } else {
                animateToRelativeAngle(OPEN_RELATIVE_ANGLE);
            }
        }
    }

    public void close() {
        if (open) {
            open = false;
            animateToRelativeAngle(0);
            movingPart.setPosition(getPosition());
        }
    }

//    public void setOpen(boolean open, boolean back) {
//        if (open) {
//            open();
//        } else {
//            close();
//        }
//    }

    protected void animateToRelativeAngle(float targetAngle) {
        this.targetRelativeAngle = targetAngle;
        startRelativeAngle = relativeAngle;
        animation = true;
        time = 0;
    }

    protected void setAngleRelative(float relativeAngle) {
        float originX = decal.getVertices()[Decal.X3];
        float originY = decal.getVertices()[Decal.Y3];

        rotOriginVector.set(originX, originY);

        rotVector.set(decal.getX(), decal.getY());
        rotVector.rotateAround(rotOriginVector, relativeAngle);

        movingPart.setX(rotVector.x);
        movingPart.setY(rotVector.y);

        movingPart.setRotation(decal.getRotation());
        movingPart.rotateY(relativeAngle);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (animation) {
            time += delta;
            if (time >= ANIM_DURATION) {
                time = ANIM_DURATION;
                animation = false;
            }
            float alpha = time / ANIM_DURATION;
            relativeAngle = Interpolation.bounceOut.apply(startRelativeAngle, targetRelativeAngle, alpha);
            setAngleRelative(relativeAngle);
        }

        if (open) {
            if (!body.getFixtureList().get(0).isSensor()) {
                body.getFixtureList().get(0).setSensor(true);
            }
        } else {
            if (body.getFixtureList().get(0).isSensor()) {
                body.getFixtureList().get(0).setSensor(false);
            }
        }
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch) {
        super.drawDecal(decalBatch);

        decalBatch.add(movingPart);
    }

    @Override
    protected void updatePosition(float originX, float originY) {
        super.updatePosition(originX, originY);
        movingPart.setPosition(originX, originY, movingPart.getZ());
    }

    @Override
    public boolean canWalk() {
        return open;
    }

    @Override
    public boolean intersectsWithOpaque(ColorUtil colorUtil, Ray ray, Vector3 boundsIntersection) {
        if (!open) {
            return true;
        }
        return super.intersectsWithOpaque(colorUtil, ray, boundsIntersection);
    }
}
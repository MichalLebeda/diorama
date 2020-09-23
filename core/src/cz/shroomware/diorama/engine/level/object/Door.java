package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.level.prototype.DoorPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class Door extends GameObject {
    private static final float ANIM_DURATION = 1;
    private static final float OPEN_RELATIVE_ANGLE = 84;
    protected boolean open = false;
    protected Decal movingPart;
    float targetRelativeAngle;
    float startRelativeAngle;
    float relativeAngle = 0;
    float time = 0;
    boolean animation;
    Vector2 rotVector = new Vector2();
    Vector2 rotOriginVector = new Vector2();

    public Door(Vector3 position,
                Quaternion quaternion,
                DoorPrototype prototype,
                BoxFactory boxFactory) {
        super(position, quaternion, prototype.getDoorPostRegion(), prototype);

        movingPart = Decal.newDecal(prototype.getDoorRegion(), true);
        movingPart.setPosition(position);
        movingPart.setRotation(decal.getRotation());
        movingPart.setWidth(prototype.getDoorRegion().getRegionWidth() / PIXELS_PER_METER);
        movingPart.setHeight(prototype.getDoorRegion().getRegionHeight() / PIXELS_PER_METER);

        attachToBody(boxFactory.addBoxCenter(decal.getX(), decal.getY(), 1, 0.1f));

//        Method method = null;
//        try {
//            // Necessary, because setRelativeAngle uses decal.getVertices()
//            // which are not updated until decal.update() which is protected
//            // TODO: find another solution
//            method = decal.getClass().getDeclaredMethod("update");
//            method.setAccessible(true);
//            Object r = method.invoke(decal);
//            method.setAccessible(false);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
    }

//    public Door(Vector3 position,
//                DoorPrototype prototype,
//                BoxFactory boxFactory) {
//        super(position, prototype.getDoorPostRegion(), prototype);
////TODO:
//
//        attachToBody(boxFactory.addBoxCenter(decal.getX(), decal.getY(), 1, 0.1f));
//    }

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

            body.getFixtureList().get(0).setSensor(true);
        }
    }

    public void close() {
        if (open) {
            open = false;
            animateToRelativeAngle(0);
            movingPart.setPosition(getPosition());
            body.getFixtureList().get(0).setSensor(false);
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
        float originX = decal.getVertices()[Decal.X1];
        float originY = decal.getVertices()[Decal.Y1];

        rotOriginVector.set(originX, originY);

        rotVector.set(decal.getX(), decal.getY());
        rotVector.rotateAround(rotOriginVector, relativeAngle);

        movingPart.setX(rotVector.x);
        movingPart.setY(rotVector.y);

        movingPart.setRotation(decal.getRotation());
        movingPart.rotateY(relativeAngle);
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        super.drawDecal(decalBatch, delta);
        decalBatch.add(movingPart);
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
    }
}
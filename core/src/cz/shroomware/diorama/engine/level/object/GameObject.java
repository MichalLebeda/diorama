package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.Identifiable;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.UpdatedDecal;
import cz.shroomware.diorama.engine.level.Floor;
import cz.shroomware.diorama.engine.level.Tile;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;
import cz.shroomware.diorama.engine.level.prototype.AtlasRegionPrototype;
import cz.shroomware.diorama.engine.level.prototype.Prototype;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public abstract class GameObject implements Identifiable {
    protected Prototype prototype = null;
    protected UpdatedDecal decal;
    protected Sprite shadowSprite;
    protected Tile tileAttachedTo = null;
    protected LogicComponent logicComponent = null;
    protected Body body = null;
    protected Identifier identifier;
    protected boolean selected = false;
    protected boolean positionDirty = false;

    protected GameObject() {
    }

    protected GameObject(Vector3 position, TextureRegion region, Prototype prototype, Identifier identifier) {
        this.prototype = prototype;
        this.identifier = identifier;

        decal = UpdatedDecal.newDecal(region, true);
        decal.rotateX(90);
        decal.setPosition(position);
        decal.setWidth(region.getRegionWidth() / PIXELS_PER_METER);
        decal.setHeight(region.getRegionHeight() / PIXELS_PER_METER);
    }

    public boolean hasLogicComponent() {
        return logicComponent != null;
    }

    public LogicComponent getLogicComponent() {
        return logicComponent;
    }

    public void attachToTile(Tile tileAttachedTo) {
        this.tileAttachedTo = tileAttachedTo;
    }

    public Tile getTileAttachedTo() {
        return tileAttachedTo;
    }

    public void sizeBoundingBox(BoundingBox boundingBox) {
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        float[] vertices = decal.getVertices();
        min.set(vertices[UpdatedDecal.X1],
                vertices[UpdatedDecal.Y1],
                vertices[UpdatedDecal.Z1]);
        max.set(vertices[UpdatedDecal.X4],
                vertices[UpdatedDecal.Y4],
                vertices[UpdatedDecal.Z4]);
        boundingBox.set(min, max);
    }

    public void update(float delta) {
        if (hasBody()) {
            if (body.getType() != BodyDef.BodyType.StaticBody && body.isAwake()) {
                positionDirty = true;
            }
        }

        if (positionDirty) {
            forceUpdatePosition();
            positionDirty = false;
        }
    }

    public void drawDecal(MinimalisticDecalBatch decalBatch) {
        //TODO: remove
//        if (!selected && tileAttachedTo != null) {
//            decal.setColor(Color.ORANGE);
//
//        }
//
        decalBatch.add(decal);
    }

    public void drawShadow(Batch spriteBatch) {
        if (shadowSprite != null) {
            //TODO update shadow sprite
            shadowSprite.draw(spriteBatch);
        }
    }

    @Override
    public String toString() {
        String string = "";

        string += prototype.getName() + " ";
        string += decal.getX() + " ";
        string += decal.getY() + " ";
        string += decal.getZ() + " ";
        string += decal.getRotation().x + " ";
        string += decal.getRotation().y + " ";
        string += decal.getRotation().z + " ";
        string += decal.getRotation().w + " ";
        string += identifier.toString();

        return string;
    }

    public Vector3 getPosition() {
        return decal.getPosition();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            decal.setColor(0.2f, 0.2f, 0.2f, 1);
        } else {
            decal.setColor(Color.WHITE);
        }
    }

    public Quaternion getRotation() {
        return decal.getRotation();
    }

    public void setRotation(Quaternion quaternion) {
        decal.setRotation(quaternion);
    }

    public boolean findIntersectionRayDecalPlane(Ray ray, UpdatedDecal testDecal, Vector3 intersection) {
        // Bottom right in decal coordinates relative to origin
        Vector3 vecA = new Vector3(testDecal.getWidth(), 0, 0);
        // Top left in decal coordinates relative to origin
        Vector3 vecB = new Vector3(0, testDecal.getHeight(), 0);

        // Apply decal rotation to our vectors
        vecA.rotate(Vector3.X, testDecal.getRotation().getAngleAround(Vector3.X));
        vecA.rotate(Vector3.Y, testDecal.getRotation().getAngleAround(Vector3.Y));
        vecA.rotate(Vector3.Z, testDecal.getRotation().getAngleAround(Vector3.Z));

        vecB.rotate(Vector3.X, testDecal.getRotation().getAngleAround(Vector3.X));
        vecB.rotate(Vector3.Y, testDecal.getRotation().getAngleAround(Vector3.Y));
        vecB.rotate(Vector3.Z, testDecal.getRotation().getAngleAround(Vector3.Z));

        // Calculate normal
        Vector3 vecNormal = vecA.crs(vecB);

        Plane plane = new Plane(vecNormal, testDecal.getPosition());
        return Intersector.intersectRayPlane(ray, plane, intersection);
    }

    public boolean isPixelOpaque(Vector3 intersection, UpdatedDecal decal) {
        decal.update();

        float[] vertices = decal.getVertices();
        Vector3 vecA = new Vector3(vertices[UpdatedDecal.X2] - vertices[UpdatedDecal.X1],
                vertices[UpdatedDecal.Y2] - vertices[UpdatedDecal.Y1],
                vertices[UpdatedDecal.Z2] - vertices[UpdatedDecal.Z1]);
        Vector3 vecB = new Vector3(vertices[UpdatedDecal.X3] - vertices[UpdatedDecal.X1],
                vertices[UpdatedDecal.Y3] - vertices[UpdatedDecal.Y1],
                vertices[UpdatedDecal.Z3] - vertices[UpdatedDecal.Z1]);
        Vector3 vecC = vecA.cpy().crs(vecB);

        Matrix3 matrix3 = new Matrix3(
                new float[]{vecA.x, vecA.y, vecA.z,
                        vecB.x, vecB.y, vecB.z,
                        vecC.x, vecC.y, vecC.z});
        matrix3.inv();

        Vector3 origin = new Vector3(vertices[UpdatedDecal.X1], vertices[UpdatedDecal.Y1], vertices[UpdatedDecal.Z1]);
        intersection.add(origin.scl(-1));
        intersection.mul(matrix3);

        TextureRegion region = decal.getTextureRegion();
        Texture texture = region.getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();

        if (intersection.x > 1) {
            return false;
        }

        if (intersection.x < 0) {
            return false;
        }

        if (intersection.y > 1) {
            return false;
        }

        if (intersection.y < 0) {
            return false;
        }

        int color = pixmap.getPixel((int) (region.getRegionX() + intersection.x * region.getRegionWidth()),
                (int) (region.getRegionY() + intersection.y * region.getRegionHeight()));

        pixmap.dispose();
        return ((color & 0x000000ff)) / 255f > 0.5f;
    }

    public boolean intersectsWithOpaque(Ray ray, Vector3 boundsIntersection) {
        return isPixelOpaque(boundsIntersection, decal);
    }

    public String getName() {
        return prototype.getName();
    }

    public void setRotationX(float angle) {
        decal.setRotationX(angle);
    }

    public void setRotationY(float angle) {
        decal.setRotationY(angle);
    }

    public void setRotationZ(float angle) {
        decal.setRotationZ(angle);
    }

    public void setRotation(float yaw, float pitch, float roll) {
        decal.setRotation(yaw, pitch, roll);
    }

    public void translate(Vector3 translation) {
        decal.translate(translation);
    }

    public void translate(float x, float y, float z) {
        decal.translate(x, y, z);
    }

    public float getHeight() {
        return decal.getHeight();
    }

    public float getWidth() {
        return decal.getWidth();
    }

    public boolean hasPrototype() {
        return prototype != null;
    }

    public Prototype getPrototype() {
        return prototype;
    }

    public void updateSurroundings(Floor floor) {
        //To be implemented specifically if needed
    }

    public boolean hasBody() {
        return body != null;
    }

    public Body getBody() {
        return body;
    }

    public void attachToBody(Body body) {
        this.body = body;
        body.setUserData(this);
    }

    protected void createShadowSprite(AtlasRegionPrototype prototype) {
        TextureRegion shadowRegion = prototype.getShadowRegion();
        if (prototype.getShadowRegion() != null) {
            shadowSprite = new Sprite(shadowRegion);
            shadowSprite.setSize(decal.getWidth() * Utils.SHADOW_SCALE, -((float) shadowSprite.getRegionHeight() / (float) shadowSprite.getRegionWidth() * decal.getWidth() * Utils.SHADOW_SCALE));
            shadowSprite.setPosition(decal.getX() - shadowSprite.getWidth() / 2, decal.getY() - shadowSprite.getHeight() - 0.01f / PIXELS_PER_METER);
        }
    }

    //TODO: fix, looks bad
    public Vector3 getNormalVector() {
        float[] vertices = decal.getVertices();
        Vector3 vecA = new Vector3(vertices[UpdatedDecal.X2] - vertices[UpdatedDecal.X1],
                vertices[UpdatedDecal.Y2] - vertices[UpdatedDecal.Y1],
                vertices[UpdatedDecal.Z2] - vertices[UpdatedDecal.Z1]);
        Vector3 vecB = new Vector3(vertices[UpdatedDecal.X3] - vertices[UpdatedDecal.X1],
                vertices[UpdatedDecal.Y3] - vertices[UpdatedDecal.Y1],
                vertices[UpdatedDecal.Z3] - vertices[UpdatedDecal.Z1]);
        return vecA.cpy().crs(vecB);
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public void setPosition(float x, float y) {
        if (hasBody()) {
            body.setTransform(x, y, body.getAngle());
        } else {
            decal.setX(x);
            decal.setY(y);
        }

        positionDirty = true;
    }

    public void setPosition(Vector2 position) {
        if (hasBody()) {
            body.setTransform(position.x, position.y, body.getAngle());
        } else {
            decal.setX(position.x);
            decal.setY(position.y);
        }

        positionDirty = true;
    }

    protected void updatePosition(float originX, float originY) {
        if (body != null) {
            decal.setX(originX);
            decal.setY(originY);
        }
        if (shadowSprite != null) {
            shadowSprite.setPosition(originX - shadowSprite.getWidth() / 2, originY - shadowSprite.getHeight() - 0.01f / PIXELS_PER_METER);
        }
    }

    public void forceUpdatePosition() {
        if (hasBody()) {
            Vector2 bodyPosition = body.getPosition();
            updatePosition(bodyPosition.x, bodyPosition.y);
        } else {
            updatePosition(decal.getX(), decal.getY());
        }
    }

    public float getX() {
        return getPosition().x;
    }

    public float getY() {
        return getPosition().y;
    }

    public float getZ() {
        return getPosition().z;
    }

    public void setZ(float z) {
        decal.setZ(z);
    }

    public void rotateY(float angle) {
        decal.rotateY(angle);
    }

    public void onContactBegin() {
    }


    public void onContactEnd() {
    }


//    public Vector2 setPositionPixelPerfect(Vector2 worldPos) {
//        // round to texels
//        // TODO MOVE OUTSIDE
//        worldPos.x = Utils.round(worldPos.x, 1f / 16f);
//        if (decal.getTextureRegion().getRegionWidth() % 2 == 1) {
//            worldPos.x += 0.5f / 16f;
//        }
//        worldPos.y = Utils.round(worldPos.y, 1f / 16f);
//
//        decal.setPosition(worldPos.x, worldPos.y, decal.getHeight() / 2);
//
//        return worldPos;
//    }
}

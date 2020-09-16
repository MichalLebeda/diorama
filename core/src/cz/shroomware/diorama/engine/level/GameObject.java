package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.io.IOException;
import java.io.OutputStream;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.Cursor;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class GameObject {
    GameObjectPrototype prototype;
    Decal decal;
    Sprite shadowSprite;
    boolean selected = false;

    public GameObject(Cursor cursor, GameObjectPrototype prototype) {
        this(cursor.getPosition(), prototype);
        decal.setRotation(cursor.getRotation());
    }

    public GameObject(Vector3 position, Quaternion quaternion, GameObjectPrototype prototype) {
        this(position, prototype);
        setRotation(quaternion);
    }

    protected GameObject(Vector3 position, GameObjectPrototype prototype) {
        this.prototype = prototype;
        TextureRegion decalRegion = prototype.getObjectRegion();
        decal = Decal.newDecal(decalRegion, true);
        decal.setPosition(position);
        decal.setWidth(decalRegion.getRegionWidth() / PIXELS_PER_METER);
        decal.setHeight(decalRegion.getRegionHeight() / PIXELS_PER_METER);

        TextureRegion shadowRegion = prototype.getShadowRegion();
        if (shadowRegion != null) {
            shadowSprite = new Sprite(shadowRegion);
            shadowSprite.setSize(decal.getWidth() * Utils.SHADOW_SCALE, -((float) shadowSprite.getRegionHeight() / (float) shadowSprite.getRegionWidth() * decal.getWidth() * Utils.SHADOW_SCALE));
            shadowSprite.setPosition(decal.getPosition().x - shadowSprite.getWidth() / 2, decal.getPosition().y - shadowSprite.getHeight() - 0.01f / PIXELS_PER_METER);
        }
    }

    public void sizeBoundingBox(BoundingBox boundingBox) {
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        float[] vertices = decal.getVertices();
        min.set(vertices[Decal.X1],
                vertices[Decal.Y1],
                vertices[Decal.Z1]);
        max.set(vertices[Decal.X4],
                vertices[Decal.Y4],
                vertices[Decal.Z4]);
        boundingBox.set(min, max);
    }

    public void drawDecal(MinimalisticDecalBatch decalBatch) {
        decalBatch.add(decal);
    }

    public void drawShadow(Batch spriteBatch) {
        if (shadowSprite != null) {
            shadowSprite.draw(spriteBatch);
        }
    }

    public void save(OutputStream outputStream) throws IOException {
        outputStream.write((prototype.getObjectRegion().name + " ").getBytes());
        outputStream.write((decal.getPosition().x + " ").getBytes());
        outputStream.write((decal.getPosition().y + " ").getBytes());
        outputStream.write((decal.getPosition().z + " ").getBytes());
        outputStream.write((decal.getRotation().x + " ").getBytes());
        outputStream.write((decal.getRotation().y + " ").getBytes());
        outputStream.write((decal.getRotation().z + " ").getBytes());
        outputStream.write((decal.getRotation().w + "\n").getBytes());
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

    //TODO: IF DECAL WAS ROTATED BY NON MULTIPLE OF 90, PASSED POSITION WILL FAIL COS BOUNDS WILL BE NON PLANAR
    public boolean isPixelOpaque(Vector3 intersection) {
//        decal.update();
        float[] vertices = decal.getVertices();
        Vector3 vecA = new Vector3(vertices[Decal.X2] - vertices[Decal.X1],
                vertices[Decal.Y2] - vertices[Decal.Y1],
                vertices[Decal.Z2] - vertices[Decal.Z1]);
        Vector3 vecB = new Vector3(vertices[Decal.X3] - vertices[Decal.X1],
                vertices[Decal.Y3] - vertices[Decal.Y1],
                vertices[Decal.Z3] - vertices[Decal.Z1]);
        Vector3 vecC = vecA.cpy().crs(vecB);

        Matrix3 matrix3 = new Matrix3(
                new float[]{vecA.x, vecA.y, vecA.z,
                        vecB.x, vecB.y, vecB.z,
                        vecC.x, vecC.y, vecC.z});
        matrix3.inv();

        Vector3 origin = new Vector3(vertices[Decal.X1], vertices[Decal.Y1], vertices[Decal.Z1]);
        intersection.add(origin.scl(-1));
        intersection.mul(matrix3);

        TextureRegion region = decal.getTextureRegion();
        Texture texture = region.getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();

        int color = pixmap.getPixel((int) (region.getRegionX() + intersection.x * region.getRegionWidth()),
                (int) (region.getRegionY() + intersection.y * region.getRegionHeight()));

        Utils.pixel = color;
        pixmap.dispose();
        return ((color & 0x000000ff)) / 255f > 0.5f;
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

    public void setRotation(Quaternion quaternion) {
        decal.setRotation(quaternion);
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
}

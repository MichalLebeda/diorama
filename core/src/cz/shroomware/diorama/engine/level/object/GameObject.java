package cz.shroomware.diorama.engine.level.object;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.Identifiable;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.Floor;
import cz.shroomware.diorama.engine.level.Tile;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;
import cz.shroomware.diorama.engine.level.prototype.AtlasRegionPrototype;
import cz.shroomware.diorama.engine.level.prototype.Prototype;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public abstract class GameObject implements Identifiable {
    protected Prototype prototype;
    protected Decal decal;
    protected Sprite shadowSprite;
    protected Tile tileAttachedTo = null;
    protected LogicComponent logicComponent = null;
    protected Body body = null;
    protected Identifier identifier = new Identifier();
    protected boolean selected = false;

    protected GameObject(Vector3 position, TextureRegion region, Prototype prototype) {
        this.prototype = prototype;

        decal = Decal.newDecal(region, true);
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
        min.set(vertices[Decal.X1],
                vertices[Decal.Y1],
                vertices[Decal.Z1]);
        max.set(vertices[Decal.X4],
                vertices[Decal.Y4],
                vertices[Decal.Z4]);
        boundingBox.set(min, max);
    }

    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
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
        if (identifier.isSet()) {
            string += prototype.getName() + ":" + identifier.getIdString() + " ";
        } else {
            string += prototype.getName() + " ";
        }
        string += decal.getX() + " ";
        string += decal.getY() + " ";
        string += decal.getZ() + " ";
        string += decal.getRotation().x + " ";
        string += decal.getRotation().y + " ";
        string += decal.getRotation().z + " ";
        string += decal.getRotation().w;
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

    //TODO: IF DECAL WAS ROTATED BY NON MULTIPLE OF 90, PASSED POSITION WILL FAIL COS BOUNDS WILL BE NON PLANAR
    public boolean isPixelOpaque(Vector3 intersection, Decal decal) {
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

        pixmap.dispose();
        return ((color & 0x000000ff)) / 255f > 0.5f;
    }

    public boolean isPixelOpaque(Vector3 intersection) {
        return isPixelOpaque(intersection, decal);
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

    public Vector3 getNormalVector() {
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        float[] vertices = decal.getVertices();
        min.set(vertices[Decal.X1],
                vertices[Decal.Y1],
                vertices[Decal.Z1]);
        max.set(vertices[Decal.X4],
                vertices[Decal.Y4],
                vertices[Decal.Z4]);
        return min.crs(max);
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public void setX(float x) {
        decal.setX(x);
    }

    public void setY(float y) {
        decal.setX(y);
    }

    public void setPosition(float x, float y) {
        decal.setPosition(x, y, decal.getZ());
    }

    public Vector2 setPositionPixelPerfect(Vector2 worldPos) {
        // round to texels
        // TODO MOVE OUTSIDE
        worldPos.x = Utils.round(worldPos.x, 1f / 16f);
        if (decal.getTextureRegion().getRegionWidth() % 2 == 1) {
            worldPos.x += 0.5f / 16f;
        }
        worldPos.y = Utils.round(worldPos.y, 1f / 16f);

        decal.setPosition(worldPos.x, worldPos.y, decal.getHeight() / 2);

        return worldPos;
    }
}

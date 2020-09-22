package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.TreeGameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class TreePrototype extends SingleRegionPrototype {
    TextureRegion leaveParticle;
    Color leaveParticleColor;

    public TreePrototype(Resources resources, TextureAtlas.AtlasRegion objectRegion) {
        super(resources, objectRegion);
        leaveParticle = resources.getObjectAtlas().findRegion("white");
        Pixmap pixmap = Utils.extractPixmapFromTextureRegion(objectRegion);
        leaveParticleColor = Utils.getDominantOpaqueColor(pixmap);
        leaveParticleColor.mul(1.6f);
    }

//    public TreePrototype(TextureAtlas.AtlasRegion objectRegion) {
//        super(objectRegion);
//    }

    @Override
    public GameObject createAt(float x, float y, Quaternion quaternion, BoxFactory boxFactory) {
        return new TreeGameObject(onFloorCoords(x, y, objectRegion), quaternion, this, boxFactory);
    }

    @Override
    public GameObject createAtCursor(GameObject cursor, BoxFactory boxFactory) {
        return new TreeGameObject(cursor.getPosition(), cursor.getRotation(), this, boxFactory);
    }

    public TextureRegion getLeaveParticle() {
        return leaveParticle;
    }

    public Color getLeaveParticleColor() {
        return leaveParticleColor;
    }
}

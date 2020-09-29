package cz.shroomware.diorama.engine.level.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.Tree;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class TreePrototype extends AtlasRegionPrototype {
    TextureRegion leaveParticle;
    Color leaveParticleColor;

    public TreePrototype(Resources resources, TextureAtlas.AtlasRegion objectRegion) {
        super(resources, objectRegion);

        Gdx.app.error("tre", objectRegion.packedWidth + "");

        leaveParticle = resources.getObjectAtlas().findRegion("white");
        Pixmap pixmap = Utils.extractPixmapFromTextureRegion(objectRegion);
        leaveParticleColor = Utils.getDominantOpaqueColor(pixmap);
        leaveParticleColor.mul(1.6f);
    }

//    public TreePrototype(TextureAtlas.AtlasRegion objectRegion) {
//        super(objectRegion);
//    }

    @Override
    public GameObject createAt(Vector3 position, BoxFactory boxFactory) {
        return new Tree(position, this, boxFactory);
    }

    public TextureRegion getLeaveParticle() {
        return leaveParticle;
    }

    public Color getLeaveParticleColor() {
        return leaveParticleColor;
    }
}

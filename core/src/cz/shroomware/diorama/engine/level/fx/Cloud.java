package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.Resources;

public class Cloud {
    protected Decal decal;
    protected float time = MathUtils.random(0f, 2f * (float) Math.PI);
    protected float speed = MathUtils.random(0.9f, 4f);
    protected Vector3 origPos;

    public Cloud(Resources resources, Vector3 position) {
        decal = Decal.newDecal(resources.getObjectAtlas().findRegions("cloud").random());
        decal.setPosition(position);
        decal.rotateX(90);
        decal.setWidth(((float) decal.getTextureRegion().getRegionWidth()) / Utils.PIXELS_PER_METER);
        decal.setHeight(((float) decal.getTextureRegion().getRegionHeight()) / Utils.PIXELS_PER_METER);
        origPos = decal.getPosition().cpy();
    }

    public void draw(MinimalisticDecalBatch decalBatch, float delta) {
        time += delta;
        decal.setX(origPos.x + (float) Math.sin(time * speed) / 30f);
        decalBatch.add(decal);
    }
}

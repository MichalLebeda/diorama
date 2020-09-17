package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.level.prototype.WallPrototype;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class WallObject extends GameObject {
    Decal left, right, front, back;

    public WallObject(Vector3 position, WallPrototype prototype) {
        super(position, prototype.getTop(), prototype);
        decal.translate(0, 0.5f, prototype.getLeftRegion().getRegionHeight() / PIXELS_PER_METER / 2f);

        left = Decal.newDecal(prototype.getLeftRegion(), true);
        left.setPosition(position.cpy().add(-0.5f, 0.5f, 0));
        left.rotateX(90);
        left.rotateY(90);
        left.setWidth(left.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        left.setHeight(left.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        right = Decal.newDecal(prototype.getRightRegion(), true);
        right.setPosition(position.cpy().add(0.5f, 0.5f, 0));
        right.rotateX(90);
        right.rotateY(90);
        right.setWidth(right.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        right.setHeight(right.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        front = Decal.newDecal(prototype.getRegion(), true);
        front.setPosition(position.cpy().add(0, 0, 0));
        front.rotateX(90);
        front.setWidth(right.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        front.setHeight(right.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);

        back = Decal.newDecal(prototype.getRegion(), true);
        back.setPosition(position.cpy().add(0, 1, 0));
        back.rotateX(90);
        back.setWidth(right.getTextureRegion().getRegionWidth() / PIXELS_PER_METER);
        back.setHeight(right.getTextureRegion().getRegionHeight() / PIXELS_PER_METER);
    }

    @Override
    public void drawDecal(MinimalisticDecalBatch decalBatch, float delta) {
        super.drawDecal(decalBatch, delta);
        decalBatch.add(left);
        decalBatch.add(right);
        decalBatch.add(front);
        decalBatch.add(back);
    }
}

package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.OpenSimplexNoise;
import cz.shroomware.diorama.engine.level.Floor;


public class Clouds {
    Array<Cloud> array = new Array<>();

    public Clouds(Floor floor, TextureAtlas atlas) {
        OpenSimplexNoise noise = new OpenSimplexNoise(39832928);


//        for (int i = 0; i < 2000; i++) {
//            Decal decal = Decal.newDecal(resources.getObjectAtlas().findRegions("cloud").random());
////            decal.setPosition(MathUtils.random(0f, Floor.GRID_SIZE + 1f),
////                    MathUtils.random(0f, Floor.GRID_SIZE + 1f),
////                    MathUtils.random(2,4f));
////            decal.setPosition(MathUtils.random(0f, Floor.GRID_SIZE + 1f),
////                    MathUtils.random(0f, Floor.GRID_SIZE + 1f),
////                    MathUtils.random(2,4f));
//            decal.rotateX(90);
//            decal.setWidth(((float) decal.getTextureRegion().getRegionWidth()) / Utils.PIXELS_PER_METER);
//            decal.setHeight(((float) decal.getTextureRegion().getRegionHeight()) / Utils.PIXELS_PER_METER);
//            array.add(decal);
//        }

        for (float y = 0; y < floor.getHeight(); y += 0.9f) {
            for (float x = 0; x < floor.getWidth(); x += 0.8f) {
                for (float z = 7; z < 10; z++) {
                    double noiseVal = noise.eval(x / 5f, y / 5f, z / 5f);
                    if (noiseVal > 0.4f) {

                        //TODO avoid glitches when two clouds on same pos
                        Cloud cloud = new Cloud(atlas, new Vector3(
                                x + MathUtils.random(-0.9f, 0.9f),
                                y + MathUtils.random(-0.9f, 0.9f),
                                z + MathUtils.random(-0.9f, 0.9f)));
                        array.add(cloud);
                    }
                }
            }
        }
    }

    public void update(float delta) {
        for (Cloud cloud : array) {
            cloud.update(delta);
        }
    }

    public void draw(MinimalisticDecalBatch decalBatch) {
        for (Cloud cloud : array) {
            cloud.draw(decalBatch);
        }
    }
}

package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.Camera;
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

        for (float y = 0; y < floor.getHeight(); y += 0.9f) {
            for (float x = 0; x < floor.getWidth(); x += 0.8f) {
                for (float z = 5; z < 7; z++) {
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

    public void draw(Camera camera, MinimalisticDecalBatch decalBatch) {
        for (Cloud cloud : array) {
            // Notice it is squared distance
            if (cloud.getPosition().dst2(camera.position) > 4000) {
                continue;
            }
            cloud.draw(decalBatch);
        }
    }
}

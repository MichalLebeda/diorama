package cz.shroomware.diorama.engine.level;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.ObjectShadowPair;
import cz.shroomware.diorama.engine.RegionAnimation;
import cz.shroomware.diorama.engine.level.prototype.AnimatedPrototype;
import cz.shroomware.diorama.engine.level.prototype.Prototype;
import cz.shroomware.diorama.engine.level.prototype.SingleRegionPrototype;

public class Prototypes {
    //TODO RENAME INSTANCES, THINK ABOUT THIS OBJECT
    protected Array<Prototype> gameObjectPrototypes = new Array<>();

    public Prototypes(Resources resources){

            Array<String> blacklist = new Array<>();
            blacklist.add("cursor");
            blacklist.add("selector_background");

            //TODO zjistit proc se neanimovane nenacitaji
            Array<TextureAtlas.AtlasRegion> regions = resources.getObjectAtlas().getRegions();
            for (TextureAtlas.AtlasRegion region : regions) {
                if (blacklist.contains(region.name, false)) {
                    continue;
                }

                Array<TextureAtlas.AtlasRegion> atlasRegions = resources.getObjectAtlas().findRegions(region.name);
                if (atlasRegions.size > 1) {
                    Array<ObjectShadowPair> pairs = new Array<ObjectShadowPair>(ObjectShadowPair.class);

                    int i = 0;
                    for (TextureAtlas.AtlasRegion atlasRegion : atlasRegions) {
                        Array<TextureAtlas.AtlasRegion> shadowRegions = resources.getShadowAtlas().findRegions(atlasRegion.name);
                        if (i < shadowRegions.size) {
                            pairs.add(new ObjectShadowPair(atlasRegion, shadowRegions.get(i)));
                        } else {
                            pairs.add(new ObjectShadowPair(atlasRegion, null));
                        }
                        i++;
                    }

                    RegionAnimation anim = new RegionAnimation(0.1f, pairs);
                    anim.setPlayMode(Animation.PlayMode.LOOP);

                    addGameObjectPrototype(new AnimatedPrototype(anim, region.name));
                } else {
                    addGameObjectPrototype(new SingleRegionPrototype(resources, region));
                }
            }
    }

    public int getSize() {
        return gameObjectPrototypes.size;
    }

    public Prototype getGameObjectPrototype(int i) {
        return gameObjectPrototypes.get(i);
    }

    public boolean exists(String name) {
        for (Prototype prototype : gameObjectPrototypes) {
            if (prototype.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean addGameObjectPrototype(Prototype objectPrototype) {
        if (!exists(objectPrototype.getName())) {
            gameObjectPrototypes.add(objectPrototype);
            return true;
        }

        return false;
    }
}

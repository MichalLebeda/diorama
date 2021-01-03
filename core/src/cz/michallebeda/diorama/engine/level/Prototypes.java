package cz.michallebeda.diorama.engine.level;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import cz.michallebeda.diorama.engine.ObjectShadowPair;
import cz.michallebeda.diorama.engine.RegionAnimation;
import cz.michallebeda.diorama.engine.level.prototype.AnimatedPrototype;
import cz.michallebeda.diorama.engine.level.prototype.AtlasRegionPrototype;
import cz.michallebeda.diorama.engine.level.prototype.DoorPrototype;
import cz.michallebeda.diorama.engine.level.prototype.EnemyPrototype;
import cz.michallebeda.diorama.engine.level.prototype.FirePrototype;
import cz.michallebeda.diorama.engine.level.prototype.LampPrototype;
import cz.michallebeda.diorama.engine.level.prototype.PillarPrototype;
import cz.michallebeda.diorama.engine.level.prototype.Prototype;
import cz.michallebeda.diorama.engine.level.prototype.TreePrototype;
import cz.michallebeda.diorama.engine.level.prototype.TriggerPrototype;
import cz.michallebeda.diorama.engine.level.prototype.WallPrototype;

public class Prototypes {
    //TODO RENAME INSTANCES, THINK ABOUT THIS OBJECT
    protected Array<cz.michallebeda.diorama.engine.level.prototype.Prototype> gameObjectPrototypes = new Array<>();

    public Prototypes(Resources resources) {
        addGameObjectPrototype(new cz.michallebeda.diorama.engine.level.prototype.WallPrototype(resources, "house_wall"));
        addGameObjectPrototype(new WallPrototype(resources, "wall"));
        addGameObjectPrototype(new TriggerPrototype(resources));
        addGameObjectPrototype(new DoorPrototype(resources));
        addGameObjectPrototype(new PillarPrototype(resources));
        addGameObjectPrototype(new LampPrototype(resources));
        addGameObjectPrototype(new FirePrototype(resources));
        addGameObjectPrototype(new EnemyPrototype(resources, "zombie"));

        Array<String> blacklist = new Array<>();
        blacklist.add("cursor");
        blacklist.add("selector_background");
        blacklist.add("wall");
        blacklist.add("door");
        blacklist.add("pillar");
        blacklist.add("lamp");
        blacklist.add("fire");

        //TODO zjistit proc se neanimovane nenacitaji
        Array<TextureAtlas.AtlasRegion> regions = resources.getObjectAtlas().getRegions();
        for (TextureAtlas.AtlasRegion region : regions) {
            boolean discard = false;
            for (String blacklistedItem : blacklist) {
                if (region.name.startsWith(blacklistedItem)) {
                    discard = true;
                    break;
                }
            }

            if (discard) {
                continue;
            }

            Array<TextureAtlas.AtlasRegion> atlasRegions = resources.getObjectAtlas().findRegions(region.name);
            if (atlasRegions.size > 1) {
                Array<ObjectShadowPair> pairs = new Array<>(ObjectShadowPair.class);

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

                RegionAnimation anim = new RegionAnimation(0.4f, pairs);
                anim.setPlayMode(Animation.PlayMode.LOOP);

                addGameObjectPrototype(new AnimatedPrototype(anim, region.name));
            } else {
                if (region.name.startsWith("tree")) {
                    addGameObjectPrototype(new TreePrototype(resources, region));
                } else {
                    addGameObjectPrototype(new AtlasRegionPrototype(resources, region));
                }
            }
        }
    }

    public int getSize() {
        return gameObjectPrototypes.size;
    }

    public cz.michallebeda.diorama.engine.level.prototype.Prototype getGameObjectPrototype(int i) {
        return gameObjectPrototypes.get(i);
    }

    public boolean exists(String name) {
        for (cz.michallebeda.diorama.engine.level.prototype.Prototype prototype : gameObjectPrototypes) {
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

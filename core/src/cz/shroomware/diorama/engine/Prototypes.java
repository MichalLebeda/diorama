package cz.shroomware.diorama.engine;

import com.badlogic.gdx.utils.Array;

public class Prototypes {
    //TODO RENAME INSTANCES, THINK ABOUT THIS OBJECT
    protected Array<GameObjectPrototype> gameObjectPrototypes = new Array<>();

    public int getSize(){
       return gameObjectPrototypes.size;
    }

    public GameObjectPrototype getGameObjectPrototype(int i){
        return gameObjectPrototypes.get(i);
    }

    public void addGameObjectProtoype(GameObjectPrototype objectPrototype){
        gameObjectPrototypes.add(objectPrototype);
    }
}

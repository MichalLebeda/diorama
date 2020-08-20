package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;

public class PlaceGameObjectAction implements HistoryAction {
    GameObject object;
    GameObjects gameObjects;

    public PlaceGameObjectAction(GameObject object, GameObjects gameObjects) {
        this.object = object;
        this.gameObjects = gameObjects;
    }

    @Override
    public void undo() {
        gameObjects.removeNoHistory(object);
    }

    @Override
    public void redo() {
        gameObjects.addNoHistory(object);
    }
}

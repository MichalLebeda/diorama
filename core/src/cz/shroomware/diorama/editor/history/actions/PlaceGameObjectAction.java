package cz.shroomware.diorama.editor.history.actions;

import cz.shroomware.diorama.engine.level.GameObject;
import cz.shroomware.diorama.engine.level.GameObjects;

public class PlaceGameObjectAction implements HistoryAction {
    GameObjects gameObjects;
    GameObject object;

    public PlaceGameObjectAction(GameObject object, GameObjects gameObjects) {
        this.object = object;
        this.gameObjects = gameObjects;
    }

    @Override
    public void undo() {
        gameObjects.remove(object);
    }

    @Override
    public void redo() {
        gameObjects.add(object);
    }

    @Override
    public String getText() {
        return "add " + object.getName();
    }
}

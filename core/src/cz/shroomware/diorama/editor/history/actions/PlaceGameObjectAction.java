package cz.shroomware.diorama.editor.history.actions;

import cz.shroomware.diorama.engine.GameObject;
import cz.shroomware.diorama.engine.GameObjects;

public class PlaceGameObjectAction implements HistoryAction {
    cz.shroomware.diorama.engine.GameObject object;
    cz.shroomware.diorama.engine.GameObjects gameObjects;

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

    @Override
    public String getText() {
        return "add "+object.getName();
    }
}

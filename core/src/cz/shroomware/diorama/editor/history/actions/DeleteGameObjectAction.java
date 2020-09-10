package cz.shroomware.diorama.editor.history.actions;

import cz.shroomware.diorama.engine.GameObject;
import cz.shroomware.diorama.engine.GameObjects;

public class DeleteGameObjectAction implements HistoryAction {
    GameObjects gameObjects;
    GameObject object;

    public DeleteGameObjectAction(GameObject object, GameObjects gameObjects) {
        this.object = object;
        this.gameObjects = gameObjects;
    }

    @Override
    public void undo() {
        gameObjects.add(object);
    }

    @Override
    public void redo() {
        gameObjects.remove(object);
    }

    @Override
    public String getText() {
        return "del " + object.getName();
    }
}

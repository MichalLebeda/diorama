package cz.shroomware.diorama.editor.history.actions;

import cz.shroomware.diorama.engine.GameObject;
import cz.shroomware.diorama.engine.GameObjects;

public class DeleteGameObjectAction implements HistoryAction {
    cz.shroomware.diorama.engine.GameObject object;
    cz.shroomware.diorama.engine.GameObjects gameObjects;

    public DeleteGameObjectAction(GameObject object, GameObjects gameObjects) {
        this.object = object;
        this.gameObjects = gameObjects;
    }

    @Override
    public void undo() {
        gameObjects.addNoHistory(object);
    }

    @Override
    public void redo() {
        gameObjects.removeNoHistory(object);
    }

    @Override
    public String getText() {
        return "del " + object.getName();
    }
}

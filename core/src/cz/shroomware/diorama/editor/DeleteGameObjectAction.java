package cz.shroomware.diorama.editor;

public class DeleteGameObjectAction implements HistoryAction {
    GameObject object;
    GameObjects gameObjects;

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

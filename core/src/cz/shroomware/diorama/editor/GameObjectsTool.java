package cz.shroomware.diorama.editor;

import cz.shroomware.diorama.editor.history.History;
import cz.shroomware.diorama.editor.history.actions.DeleteGameObjectAction;
import cz.shroomware.diorama.editor.history.actions.PlaceGameObjectAction;
import cz.shroomware.diorama.engine.level.GameObject;
import cz.shroomware.diorama.engine.level.GameObjects;

public class GameObjectsTool {
    GameObjects gameObjects;
    History history;

    public GameObjectsTool(GameObjects gameObjects, History history) {
        this.gameObjects = gameObjects;
        this.history = history;
    }

    public void addObject(GameObject gameObject) {
        gameObjects.add(gameObject);
        history.addAction(new PlaceGameObjectAction(gameObject, gameObjects));
    }

    public void removeObject(GameObject gameObject) {
        gameObjects.remove(gameObject);
        history.addAction(new DeleteGameObjectAction(gameObject, gameObjects));
    }
}

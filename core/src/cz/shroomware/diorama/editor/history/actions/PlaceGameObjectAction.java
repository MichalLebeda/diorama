package cz.shroomware.diorama.editor.history.actions;

import cz.shroomware.diorama.editor.EditorTool;
import cz.shroomware.diorama.engine.level.object.GameObject;

public class PlaceGameObjectAction implements HistoryAction {
    GameObject object;
    EditorTool editorTool;

    public PlaceGameObjectAction(GameObject object, EditorTool editorTool) {
        this.object = object;
        this.editorTool = editorTool;
    }

    @Override
    public void undo() {
        editorTool.removeObject(object, false);
    }

    @Override
    public void redo() {
        editorTool.addObject(object, false);
    }

    @Override
    public String getText() {
        return "add " + object.getName();
    }
}

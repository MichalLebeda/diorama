package cz.shroomware.diorama.editor.history.actions;

import cz.shroomware.diorama.editor.EditorTool;
import cz.shroomware.diorama.engine.level.object.GameObject;

public class DeleteGameObjectAction implements HistoryAction {
    EditorTool editorTool;
    GameObject object;

    public DeleteGameObjectAction(GameObject object, EditorTool editorTool) {
        this.object = object;
        this.editorTool = editorTool;
    }

    @Override
    public void undo() {
        editorTool.addObject(object, false);
    }

    @Override
    public void redo() {
        editorTool.removeObject(object, false);
    }

    @Override
    public String getText() {
        return "del " + object.getName();
    }
}

package cz.michallebeda.diorama.editor.history.actions;

import cz.michallebeda.diorama.editor.EditorTool;
import cz.michallebeda.diorama.engine.level.object.GameObject;

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

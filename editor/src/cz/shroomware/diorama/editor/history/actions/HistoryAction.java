package cz.shroomware.diorama.editor.history.actions;

public interface HistoryAction {
    void undo();

    void redo();

    String getText();
}

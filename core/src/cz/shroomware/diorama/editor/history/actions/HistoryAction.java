package cz.shroomware.diorama.editor.history.actions;

public interface HistoryAction {
    public void undo();

    public void redo();

    public String getText();
}

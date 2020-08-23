package cz.shroomware.diorama.editor;

public interface HistoryAction {
    public void undo();

    public void redo();

    public String getText();
}

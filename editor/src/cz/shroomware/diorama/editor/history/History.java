package cz.shroomware.diorama.editor.history;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.editor.history.actions.HistoryAction;

public class History {
    int actionIndex = -1;

    Array<HistoryAction> historyActions = new Array<>();

    public History() {

    }

    public void addAction(HistoryAction action) {
        if (actionIndex != historyActions.size - 1) {
            for (int i = historyActions.size - 1; i > actionIndex; i--) {
                historyActions.removeIndex(i);
            }
        }
        historyActions.add(action);
        actionIndex++;

        Gdx.app.log("History", "Added record, curr index " + actionIndex + " size " + historyActions.size);
    }

    public String redo() {
        if (actionIndex < historyActions.size - 1) {
            actionIndex++;
            HistoryAction historyAction = historyActions.get(actionIndex);
            historyAction.redo();
            Gdx.app.log("History", "Redo, curr index " + actionIndex + " size " + historyActions.size);

            return historyAction.getText();
        }

        return null;
    }

    public String undo() {
        if (actionIndex >= 0) {
            HistoryAction actionToUndo = historyActions.get(actionIndex);
            actionToUndo.undo();
            actionIndex--;
            Gdx.app.log("History", "Undo, curr index " + actionIndex + " size " + historyActions.size);

            return actionToUndo.getText();
        }

        return null;
    }

}

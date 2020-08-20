package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

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

        Gdx.app.error("added record", "curr index " + actionIndex + " size " + historyActions.size);
    }

    public void redo() {
        if (actionIndex < historyActions.size - 1) {
            actionIndex++;
            HistoryAction historyAction = historyActions.get(actionIndex);
            historyAction.redo();
            Gdx.app.error("redo", "curr index " + actionIndex + " size " + historyActions.size);
        }
    }

    public void undo() {
        if (actionIndex >= 0) {
            HistoryAction actionToUndo = historyActions.get(actionIndex);
            actionToUndo.undo();
            actionIndex--;
            Gdx.app.error("undo", "curr index " + actionIndex + " size " + historyActions.size);
        }
    }

}

package cz.shroomware.diorama.engine.level.object;

import cz.shroomware.diorama.editor.ui.Messages;

public interface IdManager {
    public boolean assignId(GameObject object, String id);

    public boolean assignId(GameObject object, String id, Messages messages);
}

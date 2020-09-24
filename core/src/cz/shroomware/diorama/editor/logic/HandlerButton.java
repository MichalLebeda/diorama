package cz.shroomware.diorama.editor.logic;

import com.badlogic.gdx.graphics.Color;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.ui.DFButton;

public class HandlerButton extends DFButton {
    protected Handler handler;

    public HandlerButton(EditorResources resources, Handler handler) {
        super(resources, handler.getHandlerName());
        this.handler = handler;
        label.setColor(new Color(0.4f, 1, 0.4f, 1));
    }

    public Handler getHandler() {
        return handler;
    }
}

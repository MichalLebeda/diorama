package cz.michallebeda.diorama.editor.ui.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;

import cz.michallebeda.diorama.editor.EditorResources;
import cz.michallebeda.diorama.editor.ui.DFLabel;
import cz.michallebeda.diorama.engine.level.logic.Handler;

public class HandlerButton extends HorizontalGroup {
    protected Handler handler;
    protected ConnectionSlot slot;

    public HandlerButton(EditorResources resources, Handler handler, Color color) {
        super();
        this.handler = handler;

        space(10);

        slot = new ConnectionSlot(resources, color);
        addActor(slot);

        DFLabel label = new DFLabel(resources.getSkin(), resources.getDfShader(), handler.getHandlerName());
        addActor(label);
    }

    @Override
    public boolean addListener(EventListener listener) {
        return slot.addListener(listener);
    }

    public ConnectionSlot getSlot() {
        return slot;
    }

    public Handler getHandler() {
        return handler;
    }
}

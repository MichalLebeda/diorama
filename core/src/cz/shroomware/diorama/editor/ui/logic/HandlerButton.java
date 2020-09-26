package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.ui.DFLabel;

public class HandlerButton extends HorizontalGroup {
    protected Handler handler;
    protected ConnectionSlot button;

    public HandlerButton(EditorResources resources, Handler handler, Color color) {
        super();
        this.handler = handler;

        space(10);

        button = new ConnectionSlot(resources);
        button.setColor(color);
        addActor(button);

        DFLabel label = new DFLabel(resources.getSkin(), resources.getDfShader(), handler.getHandlerName());
        addActor(label);
    }

    @Override
    public boolean addListener(EventListener listener) {
        return button.addListener(listener);
    }

    public Image getButton() {
        return button;
    }

    public Handler getHandler() {
        return handler;
    }
}

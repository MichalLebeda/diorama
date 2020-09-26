package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.ui.DFLabel;

public class EventButton extends HorizontalGroup {
    protected Event event;
    protected ConnectionSlot button;

    public EventButton(EditorResources resources, Event event, Color color) {
        super();
        this.event = event;

        space(10);

        DFLabel label = new DFLabel(resources.getSkin(), resources.getDfShader(), event.getEventName());
        addActor(label);

        button = new ConnectionSlot(resources);
        button.setColor(color);
        button.setSize(10, 10);
        addActor(button);
    }

    @Override
    public boolean addListener(EventListener listener) {
        return button.addListener(listener);
    }

    public Actor getSlot() {
        return button;
    }

    public Event getEvent() {
        return event;
    }
}

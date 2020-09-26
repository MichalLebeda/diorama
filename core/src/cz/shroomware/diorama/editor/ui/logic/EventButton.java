package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.graphics.Color;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.ui.DFButton;

public class EventButton extends DFButton {
    protected Event event;

    public EventButton(EditorResources resources, Event event, Color color) {
        super(resources.getSkin(), resources.getDfShader(), event.getEventName());
        this.event = event;
        label.setColor(color);
    }

    public Event getEvent() {
        return event;
    }
}

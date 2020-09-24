package cz.shroomware.diorama.editor.logic;

import com.badlogic.gdx.graphics.Color;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.ui.DFButton;

public class EventButton extends DFButton {
    protected Event event;

    public EventButton(EditorResources resources, Event event) {
        super(resources, event.getEventName());
        this.event = event;
        label.setColor(new Color(1, 0.4f, 0.4f, 1));
    }

    public Event getEvent() {
        return event;
    }
}

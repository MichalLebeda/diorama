package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.ui.DFLabel;

public class EventButton extends HorizontalGroup {
    protected Event event;
    protected ConnectionSlot slot;

    public EventButton(EditorResources resources, Event event, Color color) {
        super();
        this.event = event;

        space(10);

        DFLabel label = new DFLabel(resources.getSkin(), resources.getDfShader(), event.getEventName());
        addActor(label);

        slot = new ConnectionSlot(resources, color);
        slot.setSize(10, 10);
        addActor(slot);
    }

    @Override
    public boolean addListener(EventListener listener) {
        return slot.addListener(listener);
    }

    public ConnectionSlot getSlot() {
        return slot;
    }

    public Event getEvent() {
        return event;
    }
}

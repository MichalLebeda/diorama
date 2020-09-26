package cz.shroomware.diorama.engine.level.logic;

import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;

public class Event {
    cz.shroomware.diorama.engine.level.logic.component.LogicComponent parent;
    String name;

    public Event(cz.shroomware.diorama.engine.level.logic.component.LogicComponent parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getEventName() {
        return name;
    }

    public LogicComponent getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return (parent.hasId() ? parent.getId() : parent.toString()) + ":" + name;
    }
}

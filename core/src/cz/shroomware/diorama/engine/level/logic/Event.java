package cz.shroomware.diorama.engine.level.logic;

import cz.shroomware.diorama.engine.Identifiable;

public class Event {
    Identifiable parent;
    String name;

    public Event(Identifiable parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getEventName() {
        return name;
    }

    public Identifiable getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return (parent.hasId() ? parent.getId() : parent.toString()) + ":" + name;
    }
}

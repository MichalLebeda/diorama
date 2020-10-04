package cz.shroomware.diorama.engine.level.logic;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;

public class Event {
    LogicComponent parent;
    String name;

    public Event(String name) {
        this.name = name;
    }

    public void setParent(LogicComponent parent) {
        this.parent = parent;
    }

    public String getEventName() {
        return name;
    }

    public LogicComponent getParent() {
        return parent;
    }

    @Override
    public String toString() {
        Identifier parentIdentifier = parent.getIdentifier();
        return (parentIdentifier.isSet() ? parent.getIdentifier().getIdString() : parent.toString()) + ":" + name;
    }
}

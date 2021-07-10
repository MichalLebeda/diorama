package cz.michallebeda.diorama.engine.level.logic;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.component.LogicComponent;

public class Event {
    cz.michallebeda.diorama.engine.level.logic.component.LogicComponent parent;
    String name;

    public Event(String name) {
        this.name = name;
    }

    public void setParent(cz.michallebeda.diorama.engine.level.logic.component.LogicComponent parent) {
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
        return parentIdentifier.getId() + ":" + name;
    }
}

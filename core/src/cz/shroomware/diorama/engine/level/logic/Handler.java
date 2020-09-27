package cz.shroomware.diorama.engine.level.logic;

import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;

public abstract class Handler {
 LogicComponent parent;
    String name;

    public Handler(LogicComponent parent, String name) {
        this.parent = parent;
        this.name = name
                .replace(" ", "_")
                .replace(":", "_");
    }

    public String getHandlerName() {
        return name;
    }

    public LogicComponent getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return (parent.hasId() ? parent.getId() : parent.toString()) + ":" + name;
    }

    public abstract void handle();
}

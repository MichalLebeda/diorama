package cz.shroomware.diorama.engine.level.logic;

import cz.shroomware.diorama.engine.Identifiable;

public abstract class Handler {
    Identifiable parent;
    String name;

    public Handler(Identifiable parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getHandlerName() {
        return name;
    }

    public Identifiable getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return (parent.hasId() ? parent.getId() : parent.toString()) + ":" + name;
    }

    public abstract void handle();
}

package cz.shroomware.diorama.engine.level.logic;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;

public abstract class Handler {
    LogicComponent parent;
    String name;

    public Handler(String name) {
        this.name = name
                .replace(" ", "_")
                .replace(":", "_");
    }

    public void setParent(LogicComponent parent) {
        this.parent = parent;
    }

    public String getHandlerName() {
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

    public abstract void handle();

}

package cz.michallebeda.diorama.engine.level.logic;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.component.LogicComponent;

public abstract class Handler {
    cz.michallebeda.diorama.engine.level.logic.component.LogicComponent parent;
    String name;

    public Handler(String name) {
        this.name = name
                .replace(" ", "_")
                .replace(":", "_");
    }

    public void setParent(cz.michallebeda.diorama.engine.level.logic.component.LogicComponent parent) {
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

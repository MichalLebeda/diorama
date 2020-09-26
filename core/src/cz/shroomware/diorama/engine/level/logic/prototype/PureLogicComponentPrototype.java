package cz.shroomware.diorama.engine.level.logic.prototype;

import cz.shroomware.diorama.engine.level.logic.component.PureLogicComponent;

public abstract class PureLogicComponentPrototype {
    protected String name;

    public PureLogicComponentPrototype(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract PureLogicComponent create(String id);

}

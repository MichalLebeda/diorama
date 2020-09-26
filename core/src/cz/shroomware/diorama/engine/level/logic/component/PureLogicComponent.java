package cz.shroomware.diorama.engine.level.logic.component;

import cz.shroomware.diorama.engine.level.logic.prototype.PureLogicComponentPrototype;

public abstract class PureLogicComponent implements LogicComponent {
    PureLogicComponentPrototype prototype;
    String id;

    public PureLogicComponent(PureLogicComponentPrototype prototype, String id) {
        this.prototype = prototype;
        this.id = id;
    }

    public PureLogicComponentPrototype getPrototype() {
        return prototype;
    }

    @Override
    public boolean hasId() {
        return true;
    }

    @Override
    public String getId() {
        return id;
    }
}

package cz.shroomware.diorama.engine.level.logic.prototype;

import cz.shroomware.diorama.engine.level.logic.component.OrGate;
import cz.shroomware.diorama.engine.level.logic.component.PureLogicComponent;

public class OrGatePrototype extends PureLogicComponentPrototype {
    public OrGatePrototype() {
        super("OR");
    }

    @Override
    public PureLogicComponent create(String id) {
        return new OrGate(this, id);
    }
}

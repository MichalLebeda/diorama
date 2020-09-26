package cz.shroomware.diorama.engine.level.logic.prototype;

import cz.shroomware.diorama.engine.level.logic.component.AndGate;
import cz.shroomware.diorama.engine.level.logic.component.PureLogicComponent;

public class AndGatePrototype extends PureLogicComponentPrototype {
    public AndGatePrototype() {
        super("AND");
    }

    @Override
    public PureLogicComponent create(String id) {
        return new AndGate(this, id);
    }
}

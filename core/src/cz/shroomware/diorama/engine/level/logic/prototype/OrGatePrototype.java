package cz.shroomware.diorama.engine.level.logic.prototype;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.component.LogicOperator;
import cz.shroomware.diorama.engine.level.logic.component.OrGate;

public class OrGatePrototype extends LogicOperatorPrototype {
    public OrGatePrototype() {
        super("OR");
    }

    @Override
    public LogicOperator create(Identifier identifier) {
        return new OrGate(this, identifier);
    }
}

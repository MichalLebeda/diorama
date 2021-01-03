package cz.michallebeda.diorama.engine.level.logic.prototype;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.component.AndGate;
import cz.michallebeda.diorama.engine.level.logic.component.LogicOperator;

public class AndGatePrototype extends LogicOperatorPrototype {
    public AndGatePrototype() {
        super("AND");
    }

    @Override
    public LogicOperator create(Identifier identifier) {
        return new AndGate(this, identifier);
    }
}

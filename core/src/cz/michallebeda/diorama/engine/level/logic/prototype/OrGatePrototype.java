package cz.michallebeda.diorama.engine.level.logic.prototype;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.component.LogicOperator;
import cz.michallebeda.diorama.engine.level.logic.component.OrGate;

public class OrGatePrototype extends LogicOperatorPrototype {
    public OrGatePrototype() {
        super("OR");
    }

    @Override
    public LogicOperator create(Identifier identifier) {
        return new OrGate(this, identifier);
    }
}

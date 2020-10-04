package cz.shroomware.diorama.engine.level.logic.component;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.prototype.LogicOperatorPrototype;

public abstract class LogicOperator extends LogicComponent {
    LogicOperatorPrototype prototype;

    public LogicOperator(LogicOperatorPrototype prototype, Identifier identifier) {
        super(identifier);
        this.prototype = prototype;
    }

    public LogicOperatorPrototype getPrototype() {
        return prototype;
    }
}

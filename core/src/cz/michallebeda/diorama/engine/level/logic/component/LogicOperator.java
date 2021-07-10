package cz.michallebeda.diorama.engine.level.logic.component;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.prototype.LogicOperatorPrototype;

public abstract class LogicOperator extends LogicComponent {
    cz.michallebeda.diorama.engine.level.logic.prototype.LogicOperatorPrototype prototype;

    public LogicOperator(cz.michallebeda.diorama.engine.level.logic.prototype.LogicOperatorPrototype prototype, Identifier identifier) {
        super(identifier);
        this.prototype = prototype;
    }

    public LogicOperatorPrototype getPrototype() {
        return prototype;
    }
}

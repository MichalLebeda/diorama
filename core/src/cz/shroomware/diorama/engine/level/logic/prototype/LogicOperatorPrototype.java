package cz.shroomware.diorama.engine.level.logic.prototype;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.component.LogicOperator;

public abstract class LogicOperatorPrototype {
    protected String name;

    public LogicOperatorPrototype(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract LogicOperator create(Identifier identifier);

}

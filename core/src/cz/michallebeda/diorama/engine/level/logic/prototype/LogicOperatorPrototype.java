package cz.michallebeda.diorama.engine.level.logic.prototype;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.component.LogicOperator;

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

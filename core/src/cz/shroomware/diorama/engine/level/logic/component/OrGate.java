package cz.shroomware.diorama.engine.level.logic.component;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.prototype.LogicOperatorPrototype;

public class OrGate extends LogicOperator {
    cz.shroomware.diorama.engine.level.logic.Event outputTrue;
    cz.shroomware.diorama.engine.level.logic.Event outputFalse;

    boolean aValue = false;
    boolean bValue = false;

    public OrGate(LogicOperatorPrototype prototype, Identifier identifier) {
        super(prototype, identifier);

        addHandler(new cz.shroomware.diorama.engine.level.logic.Handler("set_a_true") {
            @Override
            public void handle() {
                aValue = true;
                eval();
            }
        });
        addHandler(new cz.shroomware.diorama.engine.level.logic.Handler("set_a_false") {
            @Override
            public void handle() {
                aValue = false;
                eval();
            }
        });
        addHandler(new cz.shroomware.diorama.engine.level.logic.Handler("set_b_true") {
            @Override
            public void handle() {
                bValue = true;
                eval();
            }
        });
        addHandler(new Handler("set_b_false") {
            @Override
            public void handle() {
                bValue = false;
                eval();
            }
        });

        outputTrue = new cz.shroomware.diorama.engine.level.logic.Event("output_true");
        outputFalse = new Event("output_false");
        addEvent(outputTrue);
        addEvent(outputFalse);
    }

    private void eval() {
        if (aValue || bValue) {
            logic.sendEvent(outputTrue);
        } else {
            logic.sendEvent(outputFalse);
        }
    }
}

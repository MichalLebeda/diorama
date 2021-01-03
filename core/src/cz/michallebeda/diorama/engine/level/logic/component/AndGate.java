package cz.michallebeda.diorama.engine.level.logic.component;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.Event;
import cz.michallebeda.diorama.engine.level.logic.Handler;
import cz.michallebeda.diorama.engine.level.logic.prototype.LogicOperatorPrototype;

public class AndGate extends LogicOperator {
    Event outputTrue;
    Event outputFalse;

    boolean aValue = false;
    boolean bValue = false;

    public AndGate(LogicOperatorPrototype prototype, Identifier identifier) {
        super(prototype, identifier);

        identifier.setName("AND");

        addHandler(new Handler("set_a_true") {
            @Override
            public void handle() {
                aValue = true;
                eval();
            }
        });
        addHandler(new Handler("set_a_false") {
            @Override
            public void handle() {
                aValue = false;
                eval();
            }
        });
        addHandler(new Handler("set_b_true") {
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

        outputTrue = new Event("output_true");
        outputFalse = new Event("output_false");
        addEvent(outputTrue);
        addEvent(outputFalse);
    }

    private void eval() {
        if (aValue && bValue) {
            logic.sendEvent(outputTrue);
        } else {
            logic.sendEvent(outputFalse);
        }
    }
}

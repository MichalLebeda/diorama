package cz.shroomware.diorama.engine.level.logic.component;

import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.logic.prototype.PureLogicComponentPrototype;

public class OrGate extends PureLogicComponent {
    Logic logic = null;
    String id;

    Event outputTrue;
    Event outputFalse;
    Array<Handler> handlers = new Array<>(Handler.class);
    Array<Event> events = new Array<>(Event.class);

    boolean aValue = false;
    boolean bValue = false;

    public OrGate(PureLogicComponentPrototype prototype, String id) {
        super(prototype, id);
        this.id = id;

        handlers.add(new Handler(this, "set_a_true") {
            @Override
            public void handle() {
                aValue = true;
                eval();
            }
        });
        handlers.add(new Handler(this, "set_a_false") {
            @Override
            public void handle() {
                aValue = false;
                eval();
            }
        });
        handlers.add(new Handler(this, "set_b_true") {
            @Override
            public void handle() {
                bValue = true;
                eval();
            }
        });
        handlers.add(new Handler(this, "set_b_false") {
            @Override
            public void handle() {
                bValue = false;
                eval();
            }
        });

        outputTrue = new Event(this, "output_true");
        outputFalse = new Event(this, "output_false");
        events.add(outputTrue);
        events.add(outputFalse);
    }

    private void eval() {
        if (aValue || bValue) {
            logic.sendEvent(outputTrue);
        } else {
            logic.sendEvent(outputFalse);
        }
    }

    @Override
    public Array<Event> getEvents() {
        return events;
    }

    @Override
    public Array<Handler> getHandlers() {
        return handlers;
    }

    @Override
    public void onRegister(Logic logic) {
        this.logic = logic;
    }

    @Override
    public boolean hasId() {
        return true;
    }

    @Override
    public String getId() {
        return id;
    }
}

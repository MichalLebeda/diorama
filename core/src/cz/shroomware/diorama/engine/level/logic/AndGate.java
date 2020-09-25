package cz.shroomware.diorama.engine.level.logic;

import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.Identifiable;

public class AndGate implements LogicallyRepresentable, Identifiable {
    Logic logic;
    String id;

    Event outputTrue;
    Event outputFalse;
    Array<Handler> handlers = new Array<>();

    boolean aValue = false;
    boolean bValue = false;

    public AndGate(String id) {
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

        outputFalse = new Event(this, "output_false");
        outputTrue = new Event(this, "output_true");
    }

    private void eval() {
        if (aValue && bValue) {
            logic.sendEvent(outputTrue);
        } else {
            logic.sendEvent(outputFalse);
        }
    }

    @Override
    public Array<Event> getEvents() {
        Array<Event> events = new Array<>();
        events.add(outputTrue);
        events.add(outputFalse);

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

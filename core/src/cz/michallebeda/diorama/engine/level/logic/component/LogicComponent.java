package cz.michallebeda.diorama.engine.level.logic.component;

import com.badlogic.gdx.utils.Array;

import cz.michallebeda.diorama.engine.Identifiable;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.Event;
import cz.michallebeda.diorama.engine.level.logic.Handler;
import cz.michallebeda.diorama.engine.level.logic.Logic;

public class LogicComponent implements Identifiable {
    protected Logic logic;
    private Array<Event> events = new Array<>(Event.class);
    private Array<Handler> handlers = new Array<>(Handler.class);
    private Identifier parentIdentifier;

    public LogicComponent(Identifier parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    @Override
    public Identifier getIdentifier() {
        return parentIdentifier;
    }

    public void addEvent(Event event) {
        events.add(event);
        event.setParent(this);
    }

    public void addHandler(Handler handler) {
        handlers.add(handler);
        handler.setParent(this);
    }

    public Array<Event> getEvents() {
        return events;
    }

    public Array<Handler> getHandlers() {
        return handlers;
    }

    public void onRegister(Logic logic) {
        this.logic = logic;
    }

    public boolean isRegistered() {
        return logic != null;
    }

    public Logic getLogic() {
        return logic;
    }
}

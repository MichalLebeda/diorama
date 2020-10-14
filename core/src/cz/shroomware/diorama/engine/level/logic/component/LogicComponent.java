package cz.shroomware.diorama.engine.level.logic.component;

import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.Identifiable;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;

public class LogicComponent implements Identifiable {
    protected cz.shroomware.diorama.engine.level.logic.Logic logic;
    private Array<cz.shroomware.diorama.engine.level.logic.Event> events = new Array<>(cz.shroomware.diorama.engine.level.logic.Event.class);
    private Array<cz.shroomware.diorama.engine.level.logic.Handler> handlers = new Array<>(cz.shroomware.diorama.engine.level.logic.Handler.class);
    private Identifier parentIdentifier;

    public LogicComponent(Identifier parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    @Override
    public Identifier getIdentifier() {
        return parentIdentifier;
    }

    public void addEvent(cz.shroomware.diorama.engine.level.logic.Event event) {
        events.add(event);
        event.setParent(this);
    }

    public void addHandler(cz.shroomware.diorama.engine.level.logic.Handler handler) {
        handlers.add(handler);
        handler.setParent(this);
    }

    public Array<Event> getEvents() {
        return events;
    }

    public Array<Handler> getHandlers() {
        return handlers;
    }

    public void onRegister(cz.shroomware.diorama.engine.level.logic.Logic logic) {
        this.logic = logic;
    }

    public boolean isRegistered() {
        return logic != null;
    }

    public Logic getLogic() {
        return logic;
    }
}

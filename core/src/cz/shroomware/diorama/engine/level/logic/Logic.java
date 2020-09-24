package cz.shroomware.diorama.engine.level.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cz.shroomware.diorama.engine.Identifiable;

public class Logic {
    HashMap<Identifiable, ArrayList<Event>> availableEvents = new HashMap<>();
    HashMap<Identifiable, ArrayList<Handler>> availableHandlers = new HashMap<>();
    HashMap<Event, ArrayList<Handler>> eventToHandlersConnections = new HashMap<>();
//
//    public void addEvent(Event event) {
//        availableEvents.put(event.getName(), event);
//    }
//
//    public void addHandler(Handler handler) {
//        availableHandlers.put(handler.getName(), handler);
//    }

    public void addEvents(Array<Event> events) {
        if (events != null) {
            for (Event event : events) {
                Identifiable parent = event.getParent();
                ArrayList<Event> parentsEvents;
                if (!availableEvents.containsKey(event.parent)) {
                    parentsEvents = new ArrayList<>();
                    availableEvents.put(parent, parentsEvents);
                } else {
                    parentsEvents = availableEvents.get(parent);

                    if (parentsEvents.contains(event)) {
                        Gdx.app.error("Logic", "Cannot assign same event:"
                                + event.toString()
                                + " to parent: "
                                + (parent.hasId() ? parent.getId() : parent.toString()));
                        return;
                    }
                }
                parentsEvents.add(event);
            }
        }
    }

    public void addHandlers(Array<Handler> handlers) {
        if (handlers != null) {
            for (Handler handler : handlers) {
                Identifiable parent = handler.getParent();
                ArrayList<Handler> parentsHandlers;
                if (!availableHandlers.containsKey(handler.parent)) {
                    parentsHandlers = new ArrayList<>();
                    availableHandlers.put(parent, parentsHandlers);
                } else {
                    parentsHandlers = availableHandlers.get(parent);

                    if (parentsHandlers.contains(handler)) {
                        Gdx.app.error("Logic", "Cannot assign same handler:"
                                + handler.toString()
                                + " to parent: "
                                + (parent.hasId() ? parent.getId() : parent.toString()));
                        return;
                    }
                }
                parentsHandlers.add(handler);
            }
        }
    }

    public void removeEvents(Array<Event> events) {
        if (events != null) {
            for (Event event : events) {
                availableEvents.remove(event.getEventName());
                removeConnectionsByEvent(event);
            }
        }
    }

    public void removeHandlers(Array<Handler> handlers) {
        if (handlers != null) {
            for (Handler handler : handlers) {
                availableHandlers.remove(handler.getHandlerName());
                removeConnectionByHandler(handler);
            }
        }
    }

    protected void removeConnectionsByEvent(Event event) {
        ArrayList<Handler> connectedHandlers = eventToHandlersConnections.get(event);
        if (connectedHandlers != null) {
            for (Handler handler : connectedHandlers) {
                Gdx.app.log("Logic", "removing connection from: "
                        + event.toString()
                        + " to: "
                        + handler.toString());
            }
        }
        eventToHandlersConnections.remove(event);
    }

    protected void removeConnectionByHandler(Handler handler) {
        Iterator<Map.Entry<Event, ArrayList<Handler>>> iterator = eventToHandlersConnections.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Event, ArrayList<Handler>> entry = iterator.next();

            ArrayList<Handler> connectedHandlers = entry.getValue();
            if (connectedHandlers.contains(handler)) {
                Gdx.app.log("Logic", "removing connection from: "
                        + entry.getKey().toString()
                        + " to: "
                        + entry.getValue().toString());
                connectedHandlers.remove(handler);
            }

            if (connectedHandlers.isEmpty()) {
                Gdx.app.log("Logic", "Removing  Event:"
                        + entry.getKey().toString()
                        + " because there are no connected");
                iterator.remove();
            }
        }
    }

    public HashMap<Identifiable, ArrayList<Event>> getAvailableEvents() {
        return availableEvents;
    }

    public HashMap<Identifiable, ArrayList<Handler>> getAvailableHandlers() {
        return availableHandlers;
    }

    public void sendEvent(Event event) {
        Event origEvent = availableEvents.values().iterator().next().get(0);
        if (origEvent == event) {
            Gdx.app.log("Logic", "ok");
        }
        Gdx.app.log("Logic", "Sent event: " + event.toString());


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Connections recapitulation:\n");
        for (Map.Entry<Event, ArrayList<Handler>> entry : eventToHandlersConnections.entrySet()) {
            if (event == entry.getKey()) {
                Gdx.app.log("Logic", "event matches!");
            }

            for (Handler handler : entry.getValue()) {
                stringBuilder.append(entry.getKey().toString()).append(" <----> ").append(handler.toString()).append("\n");
            }
        }
        Gdx.app.log("Level", toString());

//        if (eventToHandlersConnections.containsKey(event)) {
        ArrayList<Handler> connectedHandlers = eventToHandlersConnections.get(event);
        if (connectedHandlers != null) {
            Gdx.app.log("Logic", "Handling event: " + event.toString());
            for (Handler handler : connectedHandlers) {
                Gdx.app.log("Logic", "Calling handler: " + handler.toString());
                handler.handle();
            }

        }
    }

    public void connect(Event event, Handler handler) {
        if (eventToHandlersConnections.containsKey(event)) {
            ArrayList<Handler> connectedHandlers = eventToHandlersConnections.get(event);
            if (connectedHandlers.contains(handler)) {
                Gdx.app.error("Logic", "Cannot connect same handler:"
                        + handler.toString()
                        + " twice to event: "
                        + event.toString());
            } else {
                connectedHandlers.add(handler);
                Gdx.app.error("Logic", "Added handler: "
                        + handler.toString()
                        + " to already added event: "
                        + event.toString());
            }
        } else {
            ArrayList<Handler> connectedHandlers = new ArrayList<>();
            connectedHandlers.add(handler);
            eventToHandlersConnections.put(event, connectedHandlers);
            Gdx.app.error("Logic", "Added handler: "
                    + handler.toString()
                    + " to not handled event: "
                    + event.toString()
                    + " until now");
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Events:\n");
        for (ArrayList<Event> events : availableEvents.values()) {
            for (Event event : events) {
                stringBuilder.append(event.toString()).append("\n");
            }
        }
        stringBuilder.append("\n");

        stringBuilder.append("Handlers:\n");
        for (ArrayList<Handler> handlers : availableHandlers.values()) {
            for (Handler handler : handlers) {
                stringBuilder.append(handler.toString()).append("\n");
            }
        }
        stringBuilder.append("\n");

        stringBuilder.append("Connections:\n");
        for (Map.Entry<Event, ArrayList<Handler>> entry : eventToHandlersConnections.entrySet()) {
            for (Handler handler : entry.getValue()) {
                stringBuilder.append(entry.getKey().toString()).append(" <----> ").append(handler.toString()).append("\n");
            }
        }

        return stringBuilder.toString();
    }

    //TODO: REMOVE
    public void testConnect() {
        for (ArrayList<Event> events : availableEvents.values()) {
            for (Event event : events) {
                for (ArrayList<Handler> handlers : availableHandlers.values()) {
                    for (Handler handler : handlers) {
                        if (event.getEventName().contains("pressed") && handler.getHandlerName().contains("open")) {
                            connect(event, handler);
                        } else if (event.getEventName().contains("released") && handler.getHandlerName().contains("close")) {
                            connect(event, handler);
                        }
                    }
                }
            }
        }
    }

    public void register(LogicallyRepresentable object) {
        addEvents(object.getEvents());
        addHandlers(object.getHandlers());
        object.onRegister(this);
    }

    public void unregister(LogicallyRepresentable object) {
        removeEvents(object.getEvents());
        removeHandlers(object.getHandlers());
    }
}

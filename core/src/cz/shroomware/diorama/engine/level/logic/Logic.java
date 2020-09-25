package cz.shroomware.diorama.engine.level.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cz.shroomware.diorama.engine.Identifiable;

public class Logic {
    boolean dirty = false;
    HashMap<String, LogicallyRepresentable> registered = new HashMap<>();
    HashMap<Identifiable, ArrayList<Event>> availableEvents = new HashMap<>();
    HashMap<Identifiable, ArrayList<Handler>> availableHandlers = new HashMap<>();
    HashMap<Event, ArrayList<Handler>> eventToHandlersConnections = new HashMap<>();

    public Set<Identifiable> getAllParents() {
        Set<Identifiable> parentsSet = new HashSet<>();
        parentsSet.addAll(availableEvents.keySet());
        parentsSet.addAll(availableHandlers.keySet());

        return parentsSet;
    }

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
                availableEvents.remove(event.getParent());
                removeAllConnectionsWithEvent(event);
            }
        }
    }

    public void removeHandlers(Array<Handler> handlers) {
        if (handlers != null) {
            for (Handler handler : handlers) {
                availableHandlers.remove(handler.getParent());
                removeAllConnectionsWithHandler(handler);
            }
        }
    }

    public ArrayList<Event> getEvents(Identifiable identifiable) {
        return getAvailableEvents().get(identifiable);
    }

    public ArrayList<Handler> getHandlers(Identifiable identifiable) {
        return getAvailableHandlers().get(identifiable);
    }

    protected void removeAllConnectionsWithEvent(Event event) {
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

    protected void removeAllConnectionsWithHandler(Handler handler) {
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
        Gdx.app.log("Logic", "Logic recieved event: " + event.toString());

        ArrayList<Handler> connectedHandlers = eventToHandlersConnections.get(event);
        if (connectedHandlers != null) {
            Gdx.app.log("Logic", "Handling event: " + event.toString());
            for (Handler handler : connectedHandlers) {
                Gdx.app.log("Logic", "Calling handler: " + handler.toString());
                handler.handle();
            }
        } else {
            Gdx.app.log("Logic", "No handlers for event: " + event.toString());
        }
    }

    public void connect(Event event, Handler handler) {
        if (!event.getParent().hasId() | !handler.getParent().hasId()) {
            Gdx.app.error("Logic", "Cannot connect, missing ID form one of the objects");
            return;
        }
        if (eventToHandlersConnections.containsKey(event)) {
            ArrayList<Handler> connectedHandlers = eventToHandlersConnections.get(event);
            if (connectedHandlers.contains(handler)) {
                Gdx.app.error("Logic", "Cannot connect same handler:"
                        + handler.toString()
                        + " twice to event: "
                        + event.toString());
            } else {
                connectedHandlers.add(handler);
                dirty = true;
                Gdx.app.log("Logic", "Added handler: "
                        + handler.toString()
                        + " to already added event: "
                        + event.toString());
            }
        } else {
            ArrayList<Handler> connectedHandlers = new ArrayList<>();
            connectedHandlers.add(handler);
            eventToHandlersConnections.put(event, connectedHandlers);
            dirty = true;
            Gdx.app.log("Logic", "Added handler: "
                    + handler.toString()
                    + " to not yet handled event: "
                    + event.toString()
                    + " until now");
        }
    }


    public void disconnect(Event event, Handler handler) {
        ArrayList<Handler> connectedHandlers = eventToHandlersConnections.get(event);
        if (connectedHandlers == null) {
            Gdx.app.error("Logic", "No such handler: " + handler.toString() + " for event:" + event.toString());
            return;
        }
        connectedHandlers.remove(handler);
        dirty = true;
        Gdx.app.error("Logic", "Removed connection between: "
                + event.toString()
                + " and: "
                + handler.toString());
        if (connectedHandlers.isEmpty()) {
            Gdx.app.error("Logic", "Event: " + event.toString() + " has no connections, conn. record removed");
            eventToHandlersConnections.remove(event);
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
//                            connect(event, handler);
                        } else if (event.getEventName().contains("released") && handler.getHandlerName().contains("close")) {
//                            connect(event, handler);
                        }
                    }
                }
            }
        }
    }

    public void register(LogicallyRepresentable object) {
        //TODO use registered everywhere
        registered.put(object.getId(), object);
        addEvents(object.getEvents());
        addHandlers(object.getHandlers());
        object.onRegister(this);
    }

    public void unregister(LogicallyRepresentable object) {
        registered.remove(object.getId());
        removeEvents(object.getEvents());
        removeHandlers(object.getHandlers());
    }

    public HashMap<Event, ArrayList<Handler>> getEventToHandlersConnections() {
        return eventToHandlersConnections;
    }

    public void load(BufferedReader bufferedReader) throws IOException {
        AndGate andGate = new AndGate("and_0");
        register(andGate);
        andGate = new AndGate("and_1");
        register(andGate);

        int connectionAmount = Integer.parseInt(bufferedReader.readLine());

        String line;
        for (int i = 0; i < connectionAmount; i++) {
            line = bufferedReader.readLine();
            String[] pair = line.split(" ");
            String[] eventParts = pair[0].split(":");
            String[] handlerParts = pair[1].split(":");

            //TODO: use hashmap for getEvents/handlers
            LogicallyRepresentable eventObject = registered.get(eventParts[0]);
            Event foundEvent = null;
            Array<Event> events = eventObject.getEvents();
            for (Event event : events) {
                if (event.getEventName().equals(eventParts[1])) {
                    foundEvent = event;
                    break;
                }
            }

            LogicallyRepresentable handlerObject = registered.get(handlerParts[0]);
            Handler foundHandler = null;
            Array<Handler> handlers = handlerObject.getHandlers();
            for (Handler handler : handlers) {
                if (handler.getHandlerName().equals(handlerParts[1])) {
                    foundHandler = handler;
                    break;
                }
            }

            if (foundEvent == null || foundHandler == null) {
                Gdx.app.error("Logic", "error reading file");
            }

            connect(foundEvent, foundHandler);
        }

        dirty = false;
    }

    public void save(OutputStream outputStream) throws IOException {
        int size = 0;
        for (ArrayList<Handler> handlers : eventToHandlersConnections.values()) {
            for (Handler handler : handlers) {
                size++;
            }
        }
        outputStream.write((size + "\n").getBytes());
        for (Map.Entry<Event, ArrayList<Handler>> entry : eventToHandlersConnections.entrySet()) {
            ArrayList<Handler> handlers = entry.getValue();
            for (Handler handler : handlers) {
                outputStream.write((entry.getKey().toString() + " " + handler.toString() + "\n").getBytes());
            }
        }
        dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }
}

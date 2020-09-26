package cz.shroomware.diorama.engine.level.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Logic {
    boolean dirty = false;
    HashMap<String, LogicallyRepresentable> registered = new HashMap<>();
    HashMap<Event, ArrayList<Handler>> eventToHandlersConnections = new HashMap<>();

    public Collection<LogicallyRepresentable> getAllParents() {
        return registered.values();
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
        Gdx.app.log("Logic", "Removed connection between: "
                + event.toString()
                + " and: "
                + handler.toString());
        if (connectedHandlers.isEmpty()) {
            Gdx.app.log("Logic", "Event: " + event.toString() + " has no connections, conn. record removed");
            eventToHandlersConnections.remove(event);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Events:\n");
        for (LogicallyRepresentable logicallyRepresentable : registered.values()) {
            Array<Event> events = logicallyRepresentable.getEvents();
            for (Event event : events) {
                stringBuilder.append(event.toString()).append("\n");
            }
        }
        stringBuilder.append("\n");

        stringBuilder.append("Handlers:\n");
        for (LogicallyRepresentable logicallyRepresentable : registered.values()) {
            Array<Handler> handlers = logicallyRepresentable.getHandlers();
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

    public void register(LogicallyRepresentable object) {
        registered.put(object.getId(), object);
        object.onRegister(this);
    }

    public void unregister(LogicallyRepresentable object) {
        registered.remove(object.getId());

        Array<Event> events = object.getEvents();
        for (Event event : events) {
            removeAllConnectionsWithEvent(event);
        }

        Array<Handler> handlers = object.getHandlers();
        for (Handler handler : handlers) {
            removeAllConnectionsWithHandler(handler);
        }
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

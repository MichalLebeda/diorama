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
import java.util.Set;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;
import cz.shroomware.diorama.engine.level.logic.component.LogicOperator;
import cz.shroomware.diorama.engine.level.logic.prototype.LogicOperatorPrototype;

public class Logic {
    boolean dirty = false;
    HashMap<String, LogicComponent> registered = new HashMap<>();
    HashMap<Event, ArrayList<Handler>> eventToHandlersConnections = new HashMap<>();

    HashMap<String, LogicOperatorPrototype> nameToPureLogicPrototypes = new HashMap<>();
    HashMap<String, LogicOperator> idToLogicOperator = new HashMap<>();
    HashMap<String, Integer> prototypeNameToLastId = new HashMap<>();

    ArrayList<LogicComponent> registeredWithoutId = new ArrayList<>();

    public void addPureLogicComponentPrototype(LogicOperatorPrototype prototype) {
        if (nameToPureLogicPrototypes.containsKey(prototype.getName())) {
            Gdx.app.error("Level", "Prototype already added!!!");
        } else {
            nameToPureLogicPrototypes.put(prototype.getName(), prototype);
            prototypeNameToLastId.put(prototype.getName(), -1);
        }
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

        dirty = true;
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

        dirty = true;
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
        if (!event.getParent().getIdentifier().isSet() | !handler.getParent().getIdentifier().isSet()) {
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

    public Collection<LogicComponent> getAllParents() {
        return registered.values();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\nComponents:\n");

        Set<Map.Entry<String, LogicComponent>> entries = registered.entrySet();
        for (Map.Entry<String, LogicComponent> entry : entries) {
            stringBuilder.append("key: ");
            stringBuilder.append(entry.getKey());
            stringBuilder.append(" value: ");
            stringBuilder.append(entry.getValue().getIdentifier().getIdString());
            stringBuilder.append("\n");
        }
        stringBuilder.append("\nEvents:\n");
        for (LogicComponent logicComponent : registered.values()) {
            Array<Event> events = logicComponent.getEvents();
            if (events != null) {
                for (Event event : events) {
                    stringBuilder.append(event.toString()).append("\n");
                }
            }
        }
        stringBuilder.append("\n");

        stringBuilder.append("Handlers:\n");
        for (LogicComponent logicComponent : registered.values()) {
            Array<Handler> handlers = logicComponent.getHandlers();
            if (handlers != null) {
                for (Handler handler : handlers) {
                    stringBuilder.append(handler.toString()).append("\n");
                }
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

    /**
     * Has to be called when any component ID change occurs
     *
     * @param component Component whose ID was changed
     * @param oldId     Old ID of given component
     */
    public void componentIdChange(LogicComponent component, String oldId) {
        if (oldId != null && !oldId.isEmpty()) {
            registered.remove(oldId);
            register(component);
            Gdx.app.log("Logic", "ID of" + component.getIdentifier().getIdString() + " reregistered because of new ID");
        } else {
            if (registeredWithoutId.contains(component)) {
                registeredWithoutId.remove(component);
                Gdx.app.log("Logic", "Component has new ID so it is now properly registered: " + component.getIdentifier().getIdString());
                register(component);
            }
        }
    }

    public void register(LogicComponent component) {
        if (component.getIdentifier().isSet()) {
            if (component instanceof LogicOperator) {
                idToLogicOperator.put(component.getIdentifier().getIdString(), (LogicOperator) component);
            }
            registered.put(component.getIdentifier().getIdString(), component);
            component.onRegister(this);
            dirty = true;
        } else {
            registeredWithoutId.add(component);
            Gdx.app.log("Logic", "Added component without ID: " + component.toString());
        }
    }

    public HashMap<Event, ArrayList<Handler>> getEventToHandlersConnections() {
        return eventToHandlersConnections;
    }

    public void unregister(LogicComponent component) {
        registered.remove(component.getIdentifier().getIdString());

        Array<Event> events = component.getEvents();
        if (events != null) {
            for (Event event : events) {
                removeAllConnectionsWithEvent(event);
            }
        }

        Array<Handler> handlers = component.getHandlers();
        if (handlers != null) {
            for (Handler handler : handlers) {
                removeAllConnectionsWithHandler(handler);
            }
        }

        if (component instanceof LogicOperator) {
            idToLogicOperator.remove(component.getIdentifier().getIdString());
        }

        dirty = true;
    }

    public void save(OutputStream outputStream) throws IOException {
        int size = idToLogicOperator.size();

        String line;
        outputStream.write((size + "\n").getBytes());
        for (LogicOperator component : idToLogicOperator.values()) {
            line = component.getPrototype().getName();
            line += ":";
            line += component.getIdentifier().getIdString() + "\n";
            outputStream.write(line.getBytes());
        }

        size = 0;
        for (ArrayList<Handler> handlers : eventToHandlersConnections.values()) {
            size += handlers.size();
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

    public void load(BufferedReader bufferedReader) throws IOException {
        int pureLogicComponentsAmount = Integer.parseInt(bufferedReader.readLine());

        String line;
        for (int i = 0; i < pureLogicComponentsAmount; i++) {
            line = bufferedReader.readLine();
            String[] parts = line.split(":");

            LogicOperatorPrototype prototype = nameToPureLogicPrototypes.get(parts[0]);
            Identifier identifier = new Identifier(parts[1]);
            register(prototype.create(identifier));

            String[] idParts = parts[1].split("_");
            prototypeNameToLastId.put(prototype.getName(), Integer.parseInt(idParts[1]));
        }

        int connectionAmount = Integer.parseInt(bufferedReader.readLine());

        for (int i = 0; i < connectionAmount; i++) {
            line = bufferedReader.readLine();
            String[] pair = line.split(" ");
            String[] eventParts = pair[0].split(":");
            String[] handlerParts = pair[1].split(":");

            //TODO: use hashmap for getEvents/handlers
            LogicComponent eventObject = registered.get(eventParts[0]);
            Event foundEvent = null;
            Array<Event> events = eventObject.getEvents();
            for (Event event : events) {
                if (event.getEventName().equals(eventParts[1])) {
                    foundEvent = event;
                    break;
                }
            }

            LogicComponent handlerObject = registered.get(handlerParts[0]);
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

    public HashMap<String, LogicOperatorPrototype> getNameToPureLogicPrototypes() {
        return nameToPureLogicPrototypes;
    }

    //TODO update dirty
    public boolean isDirty() {
        return dirty;
    }

    public LogicOperator createLogicOperator(LogicOperatorPrototype prototype) {
        int index = prototypeNameToLastId.get(prototype.getName()) + 1;
        Identifier identifier = new Identifier(prototype.getName() + "_" + index);
        LogicOperator operator = prototype.create(identifier);
        register(operator);

        prototypeNameToLastId.put(prototype.getName(), index);

        return operator;
    }

//    public void addLogicOperator(LogicOperator component) {
//        idToLogicOperator.put(component.getIdentifier().getIdString(), component);

//        register(component.getLogicComponent());
//    }
}

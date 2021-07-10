package cz.michallebeda.diorama.engine.level.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

import cz.michallebeda.diorama.engine.IdGenerator;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.component.LogicComponent;
import cz.michallebeda.diorama.engine.level.logic.component.LogicOperator;
import cz.michallebeda.diorama.engine.level.logic.prototype.AndGatePrototype;
import cz.michallebeda.diorama.engine.level.logic.prototype.LogicOperatorPrototype;
import cz.michallebeda.diorama.engine.level.logic.prototype.OrGatePrototype;

public class Logic {
    boolean dirty = false;
    IdGenerator idGenerator;
    HashMap<Integer, cz.michallebeda.diorama.engine.level.logic.component.LogicComponent> registered = new HashMap<>();
    HashMap<Event, ArrayList<Handler>> eventToHandlersConnections = new HashMap<>();

    HashMap<String, cz.michallebeda.diorama.engine.level.logic.prototype.LogicOperatorPrototype> nameToPureLogicPrototypes = new HashMap<>();
    HashMap<Integer, cz.michallebeda.diorama.engine.level.logic.component.LogicOperator> idToLogicOperator = new HashMap<>();

    public Logic(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        addPureLogicComponentPrototype(new AndGatePrototype());
        addPureLogicComponentPrototype(new OrGatePrototype());
    }

    public void addPureLogicComponentPrototype(cz.michallebeda.diorama.engine.level.logic.prototype.LogicOperatorPrototype prototype) {
        if (nameToPureLogicPrototypes.containsKey(prototype.getName())) {
            Gdx.app.error("Level", "Prototype already added!!!");
        } else {
            nameToPureLogicPrototypes.put(prototype.getName(), prototype);
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
        if (event == null) {
            Gdx.app.error("Level", "called sendEvent with null");
            return;
        }

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

    public Collection<cz.michallebeda.diorama.engine.level.logic.component.LogicComponent> getAllParents() {
        return registered.values();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\nComponents:\n");

        Set<Map.Entry<Integer, cz.michallebeda.diorama.engine.level.logic.component.LogicComponent>> entries = registered.entrySet();
        for (Map.Entry<Integer, cz.michallebeda.diorama.engine.level.logic.component.LogicComponent> entry : entries) {
            stringBuilder.append("key: ");
            stringBuilder.append(entry.getKey());
            stringBuilder.append(" value: ");
            stringBuilder.append(entry.getValue().getIdentifier().toString());
            stringBuilder.append("\n");
        }
        stringBuilder.append("\nEvents:\n");
        for (cz.michallebeda.diorama.engine.level.logic.component.LogicComponent logicComponent : registered.values()) {
            Array<Event> events = logicComponent.getEvents();
            if (events != null) {
                for (Event event : events) {
                    stringBuilder.append(event.toString()).append("\n");
                }
            }
        }
        stringBuilder.append("\n");

        stringBuilder.append("Handlers:\n");
        for (cz.michallebeda.diorama.engine.level.logic.component.LogicComponent logicComponent : registered.values()) {
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

    public void register(cz.michallebeda.diorama.engine.level.logic.component.LogicComponent component) {
        if (component instanceof cz.michallebeda.diorama.engine.level.logic.component.LogicOperator) {
            idToLogicOperator.put(component.getIdentifier().getId(), (cz.michallebeda.diorama.engine.level.logic.component.LogicOperator) component);
        }
        registered.put(component.getIdentifier().getId(), component);
        component.onRegister(this);
        dirty = true;
    }

    public HashMap<Event, ArrayList<Handler>> getEventToHandlersConnections() {
        return eventToHandlersConnections;
    }

    public void unregister(cz.michallebeda.diorama.engine.level.logic.component.LogicComponent component) {
        registered.remove(component.getIdentifier().getId());

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

        if (component instanceof cz.michallebeda.diorama.engine.level.logic.component.LogicOperator) {
            idToLogicOperator.remove(component.getIdentifier().getId());
        }

        dirty = true;
    }

    public void save(FileHandle fileHandle) {
        if (isDirty()) {
            OutputStream outputStream = fileHandle.write(false);
            try {
                save(outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(OutputStream outputStream) throws IOException {
        int size = idToLogicOperator.size();

        String line;
        outputStream.write((size + "\n").getBytes());
        for (cz.michallebeda.diorama.engine.level.logic.component.LogicOperator component : idToLogicOperator.values()) {
            line = component.getPrototype().getName();
            line += ":";
            line += component.getIdentifier().getId() + "\n";
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

    public void clear() {

    }

    public void load(BufferedReader bufferedReader) throws IOException {
        int pureLogicComponentsAmount = Integer.parseInt(bufferedReader.readLine());

        String line;
        for (int i = 0; i < pureLogicComponentsAmount; i++) {
            line = bufferedReader.readLine();
            String[] parts = line.split(":");

            cz.michallebeda.diorama.engine.level.logic.prototype.LogicOperatorPrototype prototype = nameToPureLogicPrototypes.get(parts[0]);
            Identifier identifier = idGenerator.obtainLoadedIdentifier(parts[1]);
            register(prototype.create(identifier));

            String[] idParts = parts[1].split("_");
        }

        int connectionAmount = Integer.parseInt(bufferedReader.readLine());

        for (int i = 0; i < connectionAmount; i++) {
            line = bufferedReader.readLine();
            String[] pair = line.split(" ");
            String[] eventParts = pair[0].split(":");
            String[] handlerParts = pair[1].split(":");

            //TODO: use hashmap for getEvents/handlers
            cz.michallebeda.diorama.engine.level.logic.component.LogicComponent eventObject = registered.get(Integer.parseInt(eventParts[0]));
            Event foundEvent = null;
            Array<Event> events = eventObject.getEvents();
            for (Event event : events) {
                if (event.getEventName().equals(eventParts[1])) {
                    foundEvent = event;
                    break;
                }
            }

            LogicComponent handlerObject = registered.get(Integer.parseInt(handlerParts[0]));
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

    public HashMap<String, cz.michallebeda.diorama.engine.level.logic.prototype.LogicOperatorPrototype> getNameToPureLogicPrototypes() {
        return nameToPureLogicPrototypes;
    }

    //TODO update dirty
    public boolean isDirty() {
        return dirty;
    }

    public cz.michallebeda.diorama.engine.level.logic.component.LogicOperator createLogicOperator(LogicOperatorPrototype prototype) {
        Identifier identifier = idGenerator.generateId();
        LogicOperator operator = prototype.create(identifier);
        register(operator);

        return operator;
    }
}

package cz.shroomware.diorama.engine.level.logic;

public class Event {
    LogicallyRepresentable parent;
    String name;

    public Event(LogicallyRepresentable parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getEventName() {
        return name;
    }

    public LogicallyRepresentable getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return (parent.hasId() ? parent.getId() : parent.toString()) + ":" + name;
    }
}

package cz.shroomware.diorama.engine.level.logic;

public abstract class Handler {
    LogicallyRepresentable parent;
    String name;

    public Handler(LogicallyRepresentable parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getHandlerName() {
        return name;
    }

    public LogicallyRepresentable getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return (parent.hasId() ? parent.getId() : parent.toString()) + ":" + name;
    }

    public abstract void handle();
}

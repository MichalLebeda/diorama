package cz.shroomware.diorama.engine.level.portal;

import com.badlogic.gdx.math.Vector2;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.MetaLevel;
import cz.shroomware.diorama.engine.level.logic.Event;

public class MetaPortal {
    protected MetaLevel parentLevel;
    protected Identifier identifier;
    protected String name;
    protected Vector2 position;
    protected float width;
    protected float height;
    protected Event event;

    public MetaPortal(MetaLevel parentLevel, Vector2 position, float width, float height, String id) {
        this.parentLevel = parentLevel;
        this.position = position;
        this.width = width;
        this.height = height;
        this.identifier = new Identifier(id);
    }

    public MetaPortal(MetaLevel parentLevel, float x, float y, float width, float height, String id) {
        this.parentLevel = parentLevel;
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.identifier = new Identifier(id);
    }

    @Override
    public String toString() {
        String string = "";
        string += position.x + " ";
        string += position.y + " ";
        string += width + " ";
        string += height;

        if (identifier.isSet()) {
            string += " " + identifier.getIdString();
        }
        return string;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public MetaLevel getParentLevel() {
        return parentLevel;
    }
}

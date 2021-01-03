package cz.michallebeda.diorama.engine.level.portal;

import com.badlogic.gdx.math.Vector2;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.MetaLevel;

public class MetaPortal {
    protected MetaLevel parentLevel;
    protected Identifier identifier;
    protected String name;
    protected Vector2 position;
    protected float width;
    protected float height;

    public MetaPortal(MetaLevel parentLevel,
                      Vector2 position,
                      float width,
                      float height,
                      Identifier identifier) {
        this.parentLevel = parentLevel;
        this.position = position;
        this.width = width;
        this.height = height;
        this.identifier = identifier;
    }

    public MetaPortal(MetaLevel parentLevel,
                      float x,
                      float y,
                      float width,
                      float height,
                      Identifier identifier) {
        this.parentLevel = parentLevel;
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaPortal that = (MetaPortal) o;
        return identifier.getId() == that.identifier.getId();
    }

    @Override
    public int hashCode() {
        return identifier.getId();
    }

    public String getSaveString() {
        String string = position.x + " ";
        string += position.y + " ";
        string += width + " ";
        string += height;
        string += " " + identifier.toString();

        return string;
    }

    @Override
    public String toString() {
        String string = parentLevel.getName();
        string += " " + getSaveString();
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

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public MetaLevel getParentLevel() {
        return parentLevel;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }
}

package cz.shroomware.diorama.engine;

import com.badlogic.gdx.Gdx;

public class Identifier {
    private int id;
    private String name = null;

    public Identifier(int id) {
        this.id = id;
    }

    public boolean isNameSet() {
        return name != null && !name.isEmpty();
    }

    public boolean setName(String name) {
        if (name == null) {
            Gdx.app.error("Identifier", "id cannot be null");
            return false;
        }

        if (name.isEmpty()) {
            Gdx.app.error("Identifier", "id cannot be blank");
            return false;
        }

        this.name = name;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return id == that.id;
    }

    @Override
    public String toString() {
        if (isNameSet()) {
            return id + " " + name;
        } else {
            return String.valueOf(id);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

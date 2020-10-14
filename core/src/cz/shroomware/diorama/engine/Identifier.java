package cz.shroomware.diorama.engine;

import com.badlogic.gdx.Gdx;

public class Identifier {
    private String id = null;

    public Identifier() {

    }

    public Identifier(String id) {
        this.id = id;
    }

    public boolean isSet() {
        return id != null && !id.isEmpty();
    }

    // TODO: use toString() (?)!
    public String getIdString() {
        return id;
    }

    public boolean setIdString(String id) {
        if (id == null) {
            Gdx.app.error("Identifier", "id cannot be null");
            return false;
        }

        if (id.isEmpty()) {
            Gdx.app.error("Identifier", "id cannot be blank");
            return false;
        }

        this.id = id;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

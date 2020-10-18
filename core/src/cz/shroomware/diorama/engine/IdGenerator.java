package cz.shroomware.diorama.engine;

import com.badlogic.gdx.Gdx;

public class IdGenerator {
    private int lastId;
    private boolean dirty = false;

    public IdGenerator(int lastId) {
        this.lastId = lastId;
    }

    public Identifier generateId() {
        dirty = true;
        return new Identifier(++lastId);
    }

    public Identifier obtainLoadedIdentifier(String number) {
        return this.obtainLoadedIdentifier(Integer.parseInt(number));
    }

    public Identifier obtainLoadedIdentifier(int id) {
        if (id > lastId) {
            Gdx.app.error("IdGenerator", "Loaded ID cannot be greater than last ID");
            Gdx.app.error("IdGenerator", "Exiting");
            Gdx.app.exit();
        }

        return new Identifier(id);
    }

    public int getLastId() {
        return lastId;
    }

    public void setSaved() {
        dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }
}

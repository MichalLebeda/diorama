package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cz.shroomware.diorama.editor.history.History;
import cz.shroomware.diorama.engine.level.prototype.Prototype;

public class Editor {
    protected Prototype currentlySelectedPrototype;
    Mode mode = Mode.ITEM;
    History history = new History();
    String filename;

    public Editor(String filename) {
        if (filename == null) {
            throw new NullPointerException();
        }
        this.filename = filename;
    }

    public Prototype getCurrentlySelectedPrototype() {
        return currentlySelectedPrototype;
    }

    public void setCurrentlySelectedPrototype(Prototype currentlySelectedPrototype) {
        this.currentlySelectedPrototype = currentlySelectedPrototype;
        if (isMode(Editor.Mode.DELETE)) {
            setMode(Editor.Mode.ITEM);
        }
    }

    public History getHistory() {
        return history;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public boolean isMode(Mode mode) {
        return this.mode == mode;
    }

    public boolean hasSelectedPrototype() {
        return currentlySelectedPrototype != null;
    }

    public TextureRegion getPrototypeIcon() {
        if (currentlySelectedPrototype != null) {
            return currentlySelectedPrototype.getIconRegion();
        }

        return null;
    }

    public void setNextMode() {
        mode = mode.getNextMode();
    }

    Mode modeBeforeDeleteToggle = Mode.ITEM;

    public void setPrevMode() {
        mode = mode.getPrevMode();
    }

    public void toggleDelete() {
        if (mode == Mode.DELETE) {
            mode = modeBeforeDeleteToggle;
        } else {
            modeBeforeDeleteToggle = mode;
            mode = Mode.DELETE;
        }
    }

    public enum Mode {
        ITEM, DELETE, TILE, TILE_BUCKET, ID_ASSIGN;

        public Mode getNextMode() {
            return Mode.values()[(this.ordinal() + 1) % (Mode.values().length)];
        }

        public Mode getPrevMode() {
            return Mode.values()[(Mode.values().length + this.ordinal() - 1) % (Mode.values().length)];
        }
    }
}

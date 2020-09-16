package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cz.shroomware.diorama.editor.history.History;
import cz.shroomware.diorama.engine.level.GameObjectPrototype;

public class Editor {
    protected GameObjectPrototype currentlySelectedPrototype;
    Mode mode = Mode.ITEM;
    History history = new History();
    String filename;

    public Editor(String filename) {
        if (filename == null) {
            throw new NullPointerException();
        }
        this.filename = filename;
    }

    public GameObjectPrototype getCurrentlySelectedPrototype() {
        return currentlySelectedPrototype;
    }

    public void setCurrentlySelectedPrototype(GameObjectPrototype currentlySelectedPrototype) {
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

    public TextureRegion getPrototypeObjectRegion() {
        if (currentlySelectedPrototype != null) {
            return currentlySelectedPrototype.getObjectRegion();
        }

        return null;
    }

    public void setNextMode() {
        mode = mode.getNextMode();
    }

    public enum Mode {
        ITEM, DELETE, TILE, TILE_BUCKET;

        public Mode getNextMode() {
            return Mode.values()[(this.ordinal() + 1) % (Mode.values().length)];
        }
    }
}

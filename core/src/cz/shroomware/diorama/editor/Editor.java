package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cz.shroomware.diorama.Utils;

public class Editor {
    protected GameObjectPrototype currentlySelectedPrototype;
    Mode mode = Mode.PLACE;
    History history = new History();
    String filename;

    public Editor(String filename) {
        if(filename==null){
            throw new NullPointerException();
        }
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public GameObjectPrototype getCurrentlySelectedPrototype() {
        return currentlySelectedPrototype;
    }

    public void setCurrentlySelectedPrototype(GameObjectPrototype currentlySelectedPrototype) {
        this.currentlySelectedPrototype = currentlySelectedPrototype;
        mode = Mode.PLACE;
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
            return currentlySelectedPrototype.objectRegion;
        }

        return null;
    }

    public enum Mode {
        PLACE, DELETE
    }
}

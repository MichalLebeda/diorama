package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.history.History;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.prototype.Prototype;

public class Editor {
    private Prototype currentlySelectedPrototype;
    private Mode mode = Mode.ITEM;
    private History history = new History();
    private boolean hardSnap = false;
    private GameObject movedObject;

    public Editor(String filename) {
        if (filename == null) {
            throw new NullPointerException();
        }
    }

    public Prototype getCurrentlySelectedPrototype() {
        return currentlySelectedPrototype;
    }

    public void setCurrentlySelectedPrototype(Prototype currentlySelectedPrototype) {
        this.currentlySelectedPrototype = currentlySelectedPrototype;
        if (mode != Mode.TILE && mode != Mode.TILE_BUCKET) {
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
        if (mode != Mode.ITEM_MOVE) {
            movedObject = null;
        }
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

    Mode modeBeforeDeleteToggle = Mode.ITEM;

    public void setNextMode() {
        setMode(mode.getNextMode());
    }

    public void setPrevMode() {
        setMode(mode.getPrevMode());
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
        ITEM(Utils.ITEM_MODE_ICON_DRAWABLE),
        ITEM_MOVE(Utils.ITEM_MOVE_MODE_ICON_DRAWABLE),
        DELETE(Utils.DELETE_MODE_ICON_DRAWABLE),
        TILE(Utils.TILE_MODE_ICON_DRAWABLE),
        TILE_BUCKET(Utils.TILE_BUCKET_MODE_ICON_DRAWABLE),
        ID_ASSIGN(Utils.ID_ASSIGN_MODE_ICON_DRAWABLE);

        private String iconName;

        Mode(String iconName) {
            this.iconName = iconName;
        }

        public String getIconName() {
            return iconName;
        }

        public Mode getNextMode() {
            return Mode.values()[(this.ordinal() + 1) % (Mode.values().length)];
        }

        public Mode getPrevMode() {
            return Mode.values()[(Mode.values().length + this.ordinal() - 1) % (Mode.values().length)];
        }
    }

    public void toggleHardSnap() {
        hardSnap = !hardSnap;
    }

    public boolean getHardSnap() {
        return hardSnap;
    }

    public boolean isMovingObject() {
        return movedObject != null;
    }

    public GameObject getMovedObject() {
        return movedObject;
    }

    public void stopMove() {
        this.movedObject = null;
    }

    public void setMovedObject(GameObject object) {
        this.movedObject = object;
    }
}

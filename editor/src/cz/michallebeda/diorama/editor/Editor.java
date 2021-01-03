package cz.michallebeda.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import cz.michallebeda.diorama.Utils;
import cz.michallebeda.diorama.editor.history.History;
import cz.michallebeda.diorama.engine.level.object.GameObject;
import cz.michallebeda.diorama.engine.level.prototype.Prototype;

public class Editor {
    private final History history = new History();
    private final Vector2 snapOffset = new Vector2();
    Mode modeBeforeDeleteToggle = Mode.ITEM;
    private Prototype currentlySelectedPrototype;
    private Mode mode = Mode.ITEM;
    private boolean hardSnap = false;
    private boolean showLabels = false;
    private GameObject movedObject;

    public Prototype getCurrentlySelectedPrototype() {
        return currentlySelectedPrototype;
    }

    public void setCurrentlySelectedPrototype(Prototype currentlySelectedPrototype) {
        this.currentlySelectedPrototype = currentlySelectedPrototype;
        if (mode != Mode.TILE && mode != Mode.TILE_BUCKET) {
            setMode(Editor.Mode.ITEM);
        }
    }

    public float getSnapOffsetX() {
        return snapOffset.x;
    }

    public float getSnapOffsetY() {
        return snapOffset.y;
    }

    public void incrementXOffset() {
        snapOffset.x += 1f / Utils.PIXELS_PER_METER;
        if (snapOffset.x > 0.5f) {
            snapOffset.x = 0.5f;
        }
    }

    public void decrementXOffset() {
        snapOffset.x -= 1f / Utils.PIXELS_PER_METER;
        if (snapOffset.x < -0.5f) {
            snapOffset.x = -0.5f;
        }
    }

    public void incrementYOffset() {
        snapOffset.y += 1f / Utils.PIXELS_PER_METER;
        if (snapOffset.y > 0.5f) {
            snapOffset.y = 0.5f;
        }
    }

    public void decrementYOffset() {
        snapOffset.y -= 1f / Utils.PIXELS_PER_METER;
        if (snapOffset.y < -0.5f) {
            snapOffset.y = -0.5f;
        }
    }

    public void resetXOffset() {
        snapOffset.x = 0;
    }

    public void resetYOffset() {
        snapOffset.y = 0;
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

    public void toggleHardSnap() {
        hardSnap = !hardSnap;
    }

    public boolean getHardSnap() {
        return hardSnap;
    }

    public void toggleLabels() {
        showLabels = !showLabels;
    }

    public boolean getShowLabels() {
        return showLabels;
    }

    public boolean isMovingObject() {
        return movedObject != null;
    }

    public GameObject getMovedObject() {
        return movedObject;
    }

    public void setMovedObject(GameObject object) {
        this.movedObject = object;
    }

    public void stopMove() {
        this.movedObject = null;
    }

    public enum Mode {
        ITEM(Utils.ITEM_MODE_ICON_DRAWABLE),
        ITEM_MOVE(Utils.ITEM_MOVE_MODE_ICON_DRAWABLE),
        DELETE(Utils.DELETE_MODE_ICON_DRAWABLE),
        TILE(Utils.TILE_MODE_ICON_DRAWABLE),
        TILE_BUCKET(Utils.TILE_BUCKET_MODE_ICON_DRAWABLE),
        PORTAL(Utils.PORTAL_MODE_ICON_DRAWABLE),
        ID_ASSIGN(Utils.ID_ASSIGN_MODE_ICON_DRAWABLE);

        private final String iconName;

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
}

package cz.shroomware.diorama.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Editor {
    protected GameObjectPrototype currentlySelectedPrototype;
    Mode mode = Mode.PLACE;
    History history = new History();

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public GameObjectPrototype getCurrentlySelectedPrototype() {
        return currentlySelectedPrototype;
    }

    public void setCurrentlySelectedPrototype(GameObjectPrototype currentlySelectedPrototype) {
        this.currentlySelectedPrototype = currentlySelectedPrototype;
        mode = Mode.PLACE;
    }

    public enum Mode {
        PLACE, DELETE
    }

    public History getHistory() {
        return history;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isMode(Mode mode){
        return this.mode == mode;
    }

    public boolean hasSelectedPrototype(){
        return currentlySelectedPrototype != null;
    }

    public TextureRegion getPrototypeObjectRegion(){
       if(currentlySelectedPrototype!=null) {
           return currentlySelectedPrototype.objectRegion;
       }

       return null;
    }
}

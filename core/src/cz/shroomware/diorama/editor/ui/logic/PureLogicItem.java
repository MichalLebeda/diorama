package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.scenes.scene2d.Touchable;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.prototype.PureLogicComponentPrototype;
import cz.shroomware.diorama.ui.DFButton;

public class PureLogicItem extends DFButton {
    PureLogicComponentPrototype prototype;

    public PureLogicItem(EditorResources editorResources, final PureLogicComponentPrototype prototype) {
        super(editorResources.getSkin(), editorResources.getDfShader(), prototype.getName());
        this.prototype = prototype;

        setTouchable(Touchable.enabled);
    }

    public PureLogicComponentPrototype getPrototype() {
        return prototype;
    }
}

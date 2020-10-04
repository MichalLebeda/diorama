package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.scenes.scene2d.Touchable;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.prototype.LogicOperatorPrototype;
import cz.shroomware.diorama.ui.DFButton;

public class PureLogicItem extends DFButton {
    LogicOperatorPrototype prototype;

    public PureLogicItem(EditorResources editorResources, final LogicOperatorPrototype prototype) {
        super(editorResources.getSkin(), editorResources.getDfShader(), prototype.getName());
        this.prototype = prototype;

        setTouchable(Touchable.enabled);
    }

    public LogicOperatorPrototype getPrototype() {
        return prototype;
    }
}

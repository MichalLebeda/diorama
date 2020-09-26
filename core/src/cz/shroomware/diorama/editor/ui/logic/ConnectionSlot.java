package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

import cz.shroomware.diorama.editor.EditorResources;

public class ConnectionSlot extends Image {

    public ConnectionSlot(EditorResources resources) {
        super(resources.getSlotDrawable());
        setSize(10, 10);
    }
}

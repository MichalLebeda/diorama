package cz.shroomware.diorama.editor.ui.portal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.editor.ui.logic.ConnectionSlot;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.portal.MetaPortal;
import cz.shroomware.diorama.ui.DFLabel;

public class PortalButton extends HorizontalGroup {
    protected MetaPortal metaPortal;
    protected ConnectionSlot slot;

    public PortalButton(EditorResources resources, MetaPortal metaPortal, Color color) {
        super();
        this.metaPortal = metaPortal;

        space(10);

        String text;
        Identifier identifier = metaPortal.getIdentifier();

        if (identifier.isNameSet()) {
            text = identifier.getName();
        } else {
            text = String.valueOf(identifier.getId());
        }

        DFLabel label = new DFLabel(resources.getSkin(),
                resources.getDfShader(),
                text);
        addActor(label);

        slot = new ConnectionSlot(resources, color);
        slot.setSize(10, 10);
        addActor(slot);
    }

    @Override
    public boolean addListener(EventListener listener) {
        return slot.addListener(listener);
    }

    public ConnectionSlot getSlot() {
        return slot;
    }

    public MetaPortal getMetaPortal() {
        return metaPortal;
    }
}

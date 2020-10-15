package cz.shroomware.diorama.editor.ui.portal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.portal.MetaPortal;
import cz.shroomware.diorama.ui.BackgroundLabel;

public class PortalButton extends HorizontalGroup {
    protected MetaPortal metaPortal;
    protected MetaLevelBlock metaLevelBlock;
    protected BackgroundLabel label;

    public PortalButton(MetaLevelBlock metaLevelBlock, EditorResources resources, MetaPortal metaPortal, Color color) {
        super();
        this.metaLevelBlock = metaLevelBlock;
        this.metaPortal = metaPortal;

        space(10);

        String text;
        Identifier identifier = metaPortal.getIdentifier();

        if (identifier.isNameSet()) {
            text = identifier.getName();
        } else {
            text = String.valueOf(identifier.getId());
        }

        label = new BackgroundLabel(resources.getSkin(),
                resources.getDfShader(),
                text, resources.getSkin().getDrawable("dark-background-pressed"));
        label.setPad(20, 10);
        addActor(label);
    }

    @Override
    public boolean addListener(EventListener listener) {
        return label.addListener(listener);
    }

    public float getWidthPad() {
        return label.getWidthWithPadding();
    }

    public float getHeightPad() {
        return label.getHeightWithPadding();
    }

    public MetaPortal getMetaPortal() {
        return metaPortal;
    }

    public MetaLevelBlock getMetaLevelBlock() {
        return metaLevelBlock;
    }

    public void highlight() {
        // Needed for proper highlight animation
        setOrigin(Align.center);
        setTransform(true);

        clearActions();
        addAction(Actions.sequence(
                Actions.scaleTo(2, 2, 0.1f, Interpolation.circleOut),
                Actions.scaleTo(1, 1, 0.1f, Interpolation.circleIn),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        setTransform(false);
                    }
                })));
    }
}

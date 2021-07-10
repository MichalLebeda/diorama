package cz.michallebeda.diorama.editor.ui.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import cz.michallebeda.diorama.editor.EditorResources;

public class ConnectionSlot extends Container<Image> {

    public ConnectionSlot(EditorResources resources, Color color) {
        super(new Image(resources.getSlotDrawable()));

        Image image = getActor();
        image.setColor(color);
        setSize(image.getImageWidth(), image.getImageHeight());
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

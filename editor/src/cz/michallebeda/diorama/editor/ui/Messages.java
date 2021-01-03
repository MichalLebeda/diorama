package cz.michallebeda.diorama.editor.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

import cz.michallebeda.diorama.editor.EditorResources;

public class Messages extends VerticalGroup {
    EditorResources resources;

    public Messages(EditorResources resources) {
        this.resources = resources;
        columnAlign(Align.left);
        align(Align.bottomLeft);
        space(50);
        padBottom(30);
        padLeft(30);
        setRound(false);
    }

    public void showMessage(String text) {
        BackgroundLabel message = new BackgroundLabel(resources.getSkin(), resources.getDfShader(), text);
        message.setAlignment(Align.left);
        message.setFontScale(0.3f);
        Container<BackgroundLabel> container = new Container<>(message);
        container.setTransform(true);
        addActor(container);
    }

    @Override
    public void addActor(final Actor actor) {
        super.addActor(actor);
        actor.setColor(1, 1, 1, 0);
        actor.setScale(0, 0);
        actor.addAction(Actions.sequence(Actions.parallel(Actions.alpha(1, 0.6f, Interpolation.circleOut), Actions.scaleTo(1, 1, 0.6f, Interpolation.circleOut)),
                Actions.delay(3),
                Actions.parallel(Actions.alpha(0, 0.6f, Interpolation.circleIn), Actions.scaleTo(0, 0, 0.6f, Interpolation.circleIn)), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        removeActor(actor);
                    }
                })));
    }
}

package cz.shroomware.diorama.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

import cz.shroomware.diorama.DioramaGame;

public class Messages extends VerticalGroup {
    DioramaGame game;

    public Messages(DioramaGame game) {
        this.game = game;
        columnAlign(Align.left);
        align(Align.bottomLeft);
        space(30);
        padBottom(20);
        padLeft(20);
    }

    public void showMessage(String text) {
        BackgroundLabel message = new BackgroundLabel(text, game);
        message.setAlignment(Align.left);
        message.setFontScale(0.24f);
        addActor(message);
    }

    @Override
    public void addActor(final Actor actor) {
        super.addActor(actor);
        actor.setColor(1, 1, 1, 0);
        actor.addAction(Actions.sequence(Actions.alpha(1, 0.3f, Interpolation.circleIn),
                Actions.delay(3),
                Actions.alpha(0, 0.3f, Interpolation.circleOut), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        removeActor(actor);
                    }
                })));
    }
}

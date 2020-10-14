package cz.shroomware.diorama.editor.ui.portal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.editor.ui.Messages;
import cz.shroomware.diorama.engine.Project;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;
import cz.shroomware.diorama.ui.BackgroundLabel;
import cz.shroomware.diorama.ui.LeftToBackgroundLabel;

public abstract class ConnectionHud extends Stage {
    ConnectionSelectedModeIndicator connectionSelectedModeIndicator;
    Project project;
    ConnectionEditor connectionEditor;
    EditorEngineGame game;
    EditorResources editorResources;
    LeftToBackgroundLabel projectNameLabel;
    BackgroundLabel unsavedChangesLabel;
    Messages messages;
    boolean lastDirtyState = false;

    public ConnectionHud(final EditorEngineGame game, Project project, ConnectionEditor connectionEditor) {
        super();
        this.game = game;
        this.project = project;
        this.connectionEditor = connectionEditor;
        editorResources = game.getResources();

//        setDebugAll(true);

        setViewport(new ScreenViewport());
        //TODO remember why
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        connectionSelectedModeIndicator = new ConnectionSelectedModeIndicator(connectionEditor, game.getResources().getSkin());
        addActor(connectionSelectedModeIndicator);

        messages = new Messages(editorResources);
        messages.setWidth(400);
        addActor(messages);

        projectNameLabel = new LeftToBackgroundLabel(
                project.getName(),
                editorResources.getSkin(),
                editorResources.getDfShader(),
                connectionSelectedModeIndicator.getX() - 10);
        projectNameLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.back();
            }
        });
        projectNameLabel.setTouchable(Touchable.enabled);
        addActor(projectNameLabel);

        unsavedChangesLabel = new BackgroundLabel(editorResources.getSkin(), editorResources.getDfShader(), " . ");
        unsavedChangesLabel.setVisible(false);
        addActor(unsavedChangesLabel);
    }

    public void setDirty(boolean dirty) {
        if (dirty == lastDirtyState) {
            return;
        }

        if (dirty) {
            unsavedChangesLabel.setColor(1, 1, 1, 0);
            unsavedChangesLabel.setVisible(true);
            unsavedChangesLabel.clearActions();
            unsavedChangesLabel.addAction(Actions.alpha(1, 0.2f, Interpolation.circleOut));
        } else {
            unsavedChangesLabel.clearActions();
            unsavedChangesLabel.addAction(Actions.sequence(Actions.alpha(0, 0.2f, Interpolation.circleOut), Actions.run(new Runnable() {
                @Override
                public void run() {
                    unsavedChangesLabel.setVisible(false);
                }
            })));
        }

        lastDirtyState = dirty;
    }

    public void showMessage(String text) {
        messages.showMessage(text);
    }

    @Override
    public void draw() {
        super.draw();
    }

    public void hide() {
        getRoot().setVisible(false);
    }

    public void show() {
        getRoot().setVisible(true);
    }

    public void toggle() {
        Actor root = getRoot();
        root.setVisible(!root.isVisible());
    }

    public boolean isVisible() {
        return getRoot().isVisible();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);

        messages.setPosition(0, 0);

        projectNameLabel.setPosition(
                10,
                getHeight() - 10 - projectNameLabel.getHeightWithPadding());

        unsavedChangesLabel.setX(projectNameLabel.getXWithPadding() + projectNameLabel.getWidthWithPadding() + 10);
        unsavedChangesLabel.setY(projectNameLabel.getYWithPadding());

        connectionSelectedModeIndicator.setPosition(
                getWidth() - connectionSelectedModeIndicator.getWidth() - 10,
                getHeight() - connectionSelectedModeIndicator.getHeight() - 10);
    }

    //FIXME: DOESN'T WORK
    public void setScrollFocus(float x, float y) {
        setScrollFocus(hit(x, y, false));
    }

    public void cancelScrollFocus() {
        setScrollFocus(null);
    }

    public abstract void onComponentAdded(LogicComponent component);
}

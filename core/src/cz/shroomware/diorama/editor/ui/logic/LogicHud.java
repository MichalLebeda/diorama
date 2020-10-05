package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Collection;

import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.editor.ui.Messages;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;
import cz.shroomware.diorama.engine.level.logic.component.LogicOperator;
import cz.shroomware.diorama.engine.level.logic.prototype.LogicOperatorPrototype;
import cz.shroomware.diorama.ui.BackgroundLabel;
import cz.shroomware.diorama.ui.LeftToBackgroundLabel;

public abstract class LogicHud extends Stage {
    LogicSelectedModeIndicator logicSelectedModeIndicator;
    LogicEditor logicEditor;
    ScrollPane scrollPane;
    EditorEngineGame game;
    EditorResources editorResources;
    LeftToBackgroundLabel projectNameLabel;
    BackgroundLabel unsavedChangesLabel;
    Messages messages;
    boolean lastDirtyState = false;

    public LogicHud(final EditorEngineGame game, final LogicEditor logicEditor) {
        super();
        this.game = game;
        this.logicEditor = logicEditor;
        editorResources = game.getResources();

//        setDebugAll(true);

        setViewport(new ScreenViewport());
        //TODO remember why
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        final VerticalGroup itemGroup = new VerticalGroup();
        itemGroup.columnAlign(Align.right);
        itemGroup.pad(10);
        itemGroup.space(10);
        Collection<LogicOperatorPrototype> prototypes = logicEditor.getLogic().getNameToPureLogicPrototypes().values();
        for (LogicOperatorPrototype prototype : prototypes) {
            final PureLogicItem item = new PureLogicItem(editorResources, prototype);
            item.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    LogicOperator logicOperator = LogicHud.this.logicEditor.getLogic().createLogicOperator(item.getPrototype());
                    onComponentAdded(logicOperator);
                }
            });
            itemGroup.addActor(item);
        }

        scrollPane = new ScrollPane(itemGroup, game.getResources().getSkin());
        scrollPane.pack();
        addActor(scrollPane);

        logicSelectedModeIndicator = new LogicSelectedModeIndicator(logicEditor, game.getResources().getSkin());
        addActor(logicSelectedModeIndicator);

        messages = new Messages(editorResources);
        messages.setWidth(400);
        addActor(messages);

        projectNameLabel = new LeftToBackgroundLabel(
                logicEditor.levelName,
                editorResources.getSkin(),
                editorResources.getDfShader(),
                logicSelectedModeIndicator.getX() - 10);
        projectNameLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.returnToEditor();
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

    public Vector3 getMenuStagePosition() {
        Vector3 position = new Vector3();
        position.set(scrollPane.getX(), scrollPane.getY(), 0);
        return position;
    }

    public Vector3 getMenuScreenPosition() {
        return getCamera().project(getMenuStagePosition());
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


        scrollPane.setHeight(getViewport().getWorldHeight());
        scrollPane.setPosition(
                getViewport().getWorldWidth() - scrollPane.getWidth(),
                0);


        logicSelectedModeIndicator.setPosition(
                scrollPane.getX() - logicSelectedModeIndicator.getWidth() - 10,
                getHeight() - logicSelectedModeIndicator.getHeight() - 10);
    }

    public void setScrollFocus(float x, float y) {
        setScrollFocus(hit(x, y, true));
    }

    public void cancelScrollFocus() {
        setScrollFocus(null);
    }

    public abstract void onComponentAdded(LogicComponent component);
}

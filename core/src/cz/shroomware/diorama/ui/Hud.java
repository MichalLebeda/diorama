package cz.shroomware.diorama.ui;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.GameObjectPrototype;

public abstract class Hud extends Stage {
    SelectedItemIndicator selectedItemIndicator;
    ScrollPane scrollPane;
    DioramaGame game;
    LeftToBackgroundLabel projectNameLabel;
    BackgroundLabel unsavedChangesLabel;
    Messages messages;
    boolean lastDirtyState = false;

    public Hud(final DioramaGame game, Array<GameObjectPrototype> prototypes, Editor editor) {
        super();
        this.game = game;
//        setDebugAll(true);

        setViewport(new ScreenViewport());
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        getCamera().translate(getViewport().getWorldWidth() / 2,
                getViewport().getWorldHeight() / 2,
                0);

        selectedItemIndicator = new SelectedItemIndicator(editor, game.getSkin());

        final VerticalGroup itemGroup = new VerticalGroup();
        for (GameObjectPrototype prototype : prototypes) {
            itemGroup.addActor(new Item(game, editor, prototype) {
                @Override
                public float getPrefWidth() {
                    return 260;
                }
            });
        }

        itemGroup.columnAlign(Align.right);
        itemGroup.pad(10);
        itemGroup.space(10);

        scrollPane = new ScrollPane(itemGroup, game.getSkin());
        scrollPane.pack();
        scrollPane.setHeight(getViewport().getWorldHeight());
        scrollPane.setPosition(
                getViewport().getWorldWidth() - scrollPane.getWidth(),
                0);

        addActor(scrollPane);

        selectedItemIndicator.setPosition(scrollPane.getX() - selectedItemIndicator.getWidth() - 10,
                getHeight() - selectedItemIndicator.getHeight() - 10);

        addActor(selectedItemIndicator);

        ModeIndicator modeIndicator = new ModeIndicator(
                game,
                editor,
                selectedItemIndicator.getX() - 10);
        modeIndicator.setY(getHeight() - modeIndicator.getHeightWithPadding() - 10);
        addActor(modeIndicator);

        messages = new Messages(game);
        messages.setWidth(400);
        messages.setPosition(0, 0);
        addActor(messages);

        projectNameLabel = new LeftToBackgroundLabel(
                editor.getFilename(),
                game,
                selectedItemIndicator.getX() - 10);
        projectNameLabel.setPosition(
                10,
                selectedItemIndicator.getY()
                        + selectedItemIndicator.getHeight()
                        - projectNameLabel.getHeightWithPadding());
        projectNameLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.openSelection();
            }
        });
        projectNameLabel.setTouchable(Touchable.enabled);
        addActor(projectNameLabel);

        unsavedChangesLabel = new BackgroundLabel(" . ", game);
        unsavedChangesLabel.setX(projectNameLabel.getXWithPadding() + projectNameLabel.getWidthWithPadding() + 10);
        unsavedChangesLabel.setY(projectNameLabel.getYWithPadding());
        unsavedChangesLabel.setVisible(false);
        addActor(unsavedChangesLabel);
    }

    public void setDirty(boolean dirty) {
        if(dirty==lastDirtyState){
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

    public abstract void onSelectedItemRegion(GameObjectPrototype prototype);

    @Override
    public void dispose() {
        super.dispose();
    }
}

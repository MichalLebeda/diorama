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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.GameObjects;
import cz.shroomware.diorama.engine.level.prototype.Prototype;

public class Hud extends Stage {
    SelectedItemIndicator selectedItemIndicator;
    SelectedModeIndicator selectedModeIndicator;
    ModeIndicator modeIndicator;
    ScrollPane scrollPane;
    DioramaGame game;
    LeftToBackgroundLabel projectNameLabel;
    BackgroundLabel unsavedChangesLabel;
    Messages messages;
//    Image colorIndicator;
    boolean lastDirtyState = false;

    public Hud(final DioramaGame game, Prototypes prototypes, Editor editor, Level level) {
        super();
        this.game = game;
        EditorResources resources = game.getEditorResources();
//        setDebugAll(true);

        setViewport(new ScreenViewport());
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        selectedItemIndicator = new SelectedItemIndicator(editor, game.getEditorResources().getSkin());
        addActor(selectedItemIndicator);

        final VerticalGroup itemGroup = new VerticalGroup();
        itemGroup.columnAlign(Align.right);
        itemGroup.pad(10);
        itemGroup.space(10);
        for (int i = 0; i < prototypes.getSize(); i++) {
            Prototype prototype = prototypes.getGameObjectPrototype(i);
            itemGroup.addActor(new Item(game.getEditorResources(), editor, prototype) {
                @Override
                public float getPrefWidth() {
                    return 260;
                }
            });
        }

        scrollPane = new ScrollPane(itemGroup, game.getEditorResources().getSkin());
        scrollPane.pack();
        addActor(scrollPane);

        selectedModeIndicator = new SelectedModeIndicator(editor, game.getEditorResources().getSkin());
        addActor(selectedModeIndicator);

        modeIndicator = new ModeIndicator(
                resources,
                editor,
                selectedModeIndicator.getX() - 10);
//        modeIndicator.setY(getHeight() - modeIndicator.getHeightWithPadding() - 10);
//        //TODO: remove
//        addActor(modeIndicator);

        messages = new Messages(resources);
        messages.setWidth(400);
        addActor(messages);

        projectNameLabel = new LeftToBackgroundLabel(
                level.getFilename(),
                resources.getSkin(),
                resources.getDfShader(),
                selectedItemIndicator.getX() - 10);
        projectNameLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.openSelection();
            }
        });
        projectNameLabel.setTouchable(Touchable.enabled);
        addActor(projectNameLabel);

        unsavedChangesLabel = new BackgroundLabel(" . ", resources.getSkin(), resources.getDfShader());
        unsavedChangesLabel.setVisible(false);
        addActor(unsavedChangesLabel);

//        colorIndicator = new Image(game.getEditorResources().getUiAtlas().findRegion("white")) {
//            @Override
//            public float getMinHeight() {
//                return 100;
//            }
//
//            @Override
//            public float getMaxHeight() {
//                return 100;
//            }
//        };
//        colorIndicator.setSize(100, 100);
//        addActor(colorIndicator);
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

        modeIndicator.setY(getHeight() - modeIndicator.getHeightWithPadding() - 10);

        scrollPane.setHeight(getViewport().getWorldHeight());
        scrollPane.setPosition(
                getViewport().getWorldWidth() - scrollPane.getWidth(),
                0);

        selectedItemIndicator.setPosition(scrollPane.getX() - selectedItemIndicator.getWidth() - 10,
                getHeight() - selectedItemIndicator.getHeight() - 10);

        selectedModeIndicator.setPosition(
                selectedItemIndicator.getX() - selectedModeIndicator.getWidth() - 10,
                selectedItemIndicator.getY());
    }

    public void openIdAssignDialog(final GameObjects gameObjects, final GameObject gameObject) {
        NameDialog nameDialog = new NameDialog(game,
                "Set Object Tag:",
                gameObject.hasId() ? gameObject.getId() : "") {
            @Override
            public void onAccepted(String name) {
                gameObjects.assignId(gameObject, name);
                Gdx.app.error("Hud", "assigned id " + gameObject.toString());
            }
        };

        nameDialog.show(this);
    }

    public void setScrollFocus(boolean focus) {
        if (focus) {
            setScrollFocus(scrollPane);
        } else {
            setScrollFocus(null);
        }
    }
}

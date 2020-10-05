package cz.shroomware.diorama.editor.ui;

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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.Prototypes;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.level.object.GameObjects;
import cz.shroomware.diorama.engine.level.prototype.Prototype;
import cz.shroomware.diorama.ui.BackgroundLabel;
import cz.shroomware.diorama.ui.LeftToBackgroundLabel;
import cz.shroomware.diorama.ui.NameDialog;

public class Hud extends Stage {
    EditorEngineGame game;
    SelectedItemIndicator selectedItemIndicator;
    SelectedModeIndicator selectedModeIndicator;
    IconButton logicEditorButton;
    ScrollPane scrollPane;
    LeftToBackgroundLabel projectNameLabel;
    BackgroundLabel unsavedChangesLabel;
    Messages messages;
    EditorResources resources;
    //    Image colorIndicator;
    boolean lastDirtyState = false;

    public Hud(final EditorEngineGame game, Prototypes prototypes, final Editor editor, final Level level) {
        super();
        this.game = game;
        resources = game.getResources();

//        setDebugAll(true);

        setViewport(new ScreenViewport());
        //TODO remember why
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        selectedItemIndicator = new SelectedItemIndicator(editor, resources.getSkin());
        addActor(selectedItemIndicator);

        final VerticalGroup itemGroup = new VerticalGroup();
        itemGroup.columnAlign(Align.right);
        itemGroup.pad(10);
        itemGroup.space(10);
        for (int i = 0; i < prototypes.getSize(); i++) {
            Prototype prototype = prototypes.getGameObjectPrototype(i);
            itemGroup.addActor(new Item(resources.getSkin(), resources.getDfShader(), editor, prototype) {
                @Override
                public float getPrefWidth() {
                    return 260;
                }
            });
        }

        scrollPane = new ScrollPane(itemGroup, resources.getSkin());
        scrollPane.pack();
        addActor(scrollPane);

        selectedModeIndicator = new SelectedModeIndicator(editor, resources.getSkin());
        addActor(selectedModeIndicator);

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

        unsavedChangesLabel = new BackgroundLabel(resources.getSkin(), resources.getDfShader(), " . ");
        unsavedChangesLabel.setVisible(false);
        addActor(unsavedChangesLabel);

        Drawable logicEditorIcon = resources.getSkin().getDrawable(Utils.CONNECT_MODE_ICON_DRAWABLE);
        logicEditorButton = new IconButton(resources.getSkin(), logicEditorIcon);
        logicEditorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.openLogicEditor(level.getFilename(), level.getLogic());
            }
        });

        addActor(logicEditorButton);


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

        scrollPane.setHeight(getViewport().getWorldHeight());
        scrollPane.setPosition(
                getViewport().getWorldWidth() - scrollPane.getWidth(),
                0);

        selectedItemIndicator.setPosition(scrollPane.getX() - selectedItemIndicator.getWidth() - 10,
                getHeight() - selectedItemIndicator.getHeight() - 10);

        selectedModeIndicator.setPosition(
                selectedItemIndicator.getX() - selectedModeIndicator.getWidth() - 10,
                selectedItemIndicator.getY());

        logicEditorButton.setPosition(
                selectedModeIndicator.getX() - logicEditorButton.getWidth() - 10,
                selectedModeIndicator.getY());
    }

    public void openIdAssignDialog(final GameObjects gameObjects, final GameObject gameObject) {
        NameDialog nameDialog = new NameDialog(resources.getSkin(),
                resources.getDfShader(),
                "Set Object Tag:",
                gameObject.getIdentifier().isSet() ? gameObject.getIdentifier().getIdString() : "") {
            @Override
            public void onAccepted(String name) {
                gameObjects.assignId(gameObject, name, messages);
            }
        };

        nameDialog.show(this);
    }

    public void setScrollFocus(float x, float y) {
        setScrollFocus(hit(x, y, true));
    }

    public void cancelScrollFocus() {
        setScrollFocus(null);
    }
}

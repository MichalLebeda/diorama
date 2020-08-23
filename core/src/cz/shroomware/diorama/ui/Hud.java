package cz.shroomware.diorama.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
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
    LeftToBackgroundLabel saveFileLabel;

    public Hud(DioramaGame game, Array<GameObjectPrototype> prototypes, Editor editor) {
        super();
        this.game = game;
//        setDebugAll(true);

        setViewport(new ScreenViewport());
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        getCamera().translate(getViewport().getWorldWidth() / 2,
                getViewport().getWorldHeight() / 2,
                0);

        selectedItemIndicator = new SelectedItemIndicator(editor, game.getDarkBackground());

        VerticalGroup itemGroup = new VerticalGroup();
        for (GameObjectPrototype prototype : prototypes) {
            itemGroup.addActor(new Item(game, editor, prototype));
        }

        itemGroup.columnAlign(Align.right);
        itemGroup.pad(20);
        itemGroup.space(20);

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

        saveFileLabel = new LeftToBackgroundLabel(
                editor.getFilename(),
                game,
                selectedItemIndicator.getX() - 10);

        saveFileLabel.setPosition(
                10,
                selectedItemIndicator.getY()
                        + selectedItemIndicator.getHeight()
                        - saveFileLabel.getHeightWithPadding());
        addActor(saveFileLabel);

        messages = new Messages(game);
        messages.setWidth(400);
//        messages.setHeight(200);

        messages.setPosition(0,0);
        addActor(messages);
    }

    Messages messages;

    public void showMessage(String text){
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

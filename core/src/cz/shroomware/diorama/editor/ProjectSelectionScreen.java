package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.ui.BackgroundLabel;
import cz.shroomware.diorama.ui.DFLabel;
import cz.shroomware.diorama.ui.FilenameDialog;

public class ProjectSelectionScreen implements Screen {
    DioramaGame game;
    Stage stage;
    Color backgroundColor = new Color(0x909090ff);

    public ProjectSelectionScreen(final DioramaGame game) {
        this.game = game;

        stage = new Stage();
//        stage.setDebugAll(true);
        stage.setViewport(new ScreenViewport());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getCamera().translate(stage.getViewport().getWorldWidth() / 2,
                stage.getViewport().getWorldHeight() / 2,
                0);

        VerticalGroup verticalGroup = new VerticalGroup();

        FileHandle[] fileHandles = Gdx.files.external(Utils.PROJECT_FOLDER).list();
        for (final FileHandle fileHandle : fileHandles) {
            HorizontalGroup horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new DFLabel(fileHandle.name(), game));
            horizontalGroup.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.openEditor(fileHandle.name());
                }
            });
            verticalGroup.addActor(horizontalGroup);
        }

        verticalGroup.columnAlign(Align.left);
        verticalGroup.align(Align.topLeft);
        verticalGroup.pad(20);
        verticalGroup.space(20);

        ScrollPane scrollPane = new ScrollPane(verticalGroup, game.getSkin());
        scrollPane.pack();
        scrollPane.setWidth(stage.getWidth() / 2);
        scrollPane.setHeight(stage.getHeight() * 0.8f);
        scrollPane.setPosition(
                stage.getWidth() / 2 - scrollPane.getWidth() / 2,
                stage.getHeight() / 2 - scrollPane.getHeight() / 2);

        BackgroundLabel createFileLabel = new BackgroundLabel(
                "Create new file",
                game);

        createFileLabel.setPosition(
                stage.getWidth() - createFileLabel.getWidthWithPadding(),
                stage.getHeight() - createFileLabel.getHeightWithPadding());
        createFileLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FilenameDialog dialog = new FilenameDialog(game);
                dialog.show(stage);
                super.clicked(event, x, y);
            }
        });
        stage.addActor(createFileLabel);

        stage.addActor(scrollPane);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

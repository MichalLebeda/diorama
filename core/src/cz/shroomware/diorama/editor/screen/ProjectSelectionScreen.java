package cz.shroomware.diorama.editor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.editor.ui.ProjectButton;
import cz.shroomware.diorama.ui.BackgroundLabel;
import cz.shroomware.diorama.ui.NameDialog;

public class ProjectSelectionScreen implements Screen {
    final VerticalGroup verticalGroup;
    ScrollPane scrollPane;
    BackgroundLabel createFileLabel;
    EditorEngineGame game;
    EditorResources resources;
    Stage stage;
    Color backgroundColor = new Color(0x424242ff);


    public ProjectSelectionScreen(final EditorEngineGame game) {
        this.game = game;
        resources = game.getResources();

        stage = new Stage(new ScreenViewport());
//        stage.setDebugAll(true);

        verticalGroup = new VerticalGroup();
        verticalGroup.columnAlign(Align.left);
        verticalGroup.align(Align.topLeft);
        verticalGroup.pad(20);
        verticalGroup.space(20);

        scrollPane = new ScrollPane(verticalGroup);
        stage.addActor(scrollPane);
        createFileLabel = new BackgroundLabel(
                resources.getSkin(), resources.getDfShader(), "New File"
        );

        createFileLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NameDialog dialog = new NameDialog(resources.getSkin(), resources.getDfShader(), "New Project Name: ", "") {
                    @Override
                    public void onAccepted(String name) {
                        game.openEditor(name);
                    }
                };
                dialog.show(stage);
                super.clicked(event, x, y);
            }
        });
        stage.addActor(createFileLabel);
    }

    public void refreshList() {
        verticalGroup.clear();

        FileHandle[] fileHandles = Utils.getProjectFolderFileHandle().list();
        for (final FileHandle fileHandle : fileHandles) {
            ProjectButton horizontalGroup = new ProjectButton(game, fileHandle) {
                @Override
                public void onDelete(ProjectButton button) {
                    verticalGroup.removeActor(button);
                }
            };
            verticalGroup.addActor(horizontalGroup);
        }

        updateScrollPaneSize();
    }

    @Override
    public void show() {
        //TODO workaround, show called before resize
//        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        Gdx.input.setInputProcessor(stage);
        //TODO see resize, workaround
//        refreshList();
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
        stage.getViewport().update(width, height, true);

        createFileLabel.setPosition(
                stage.getWidth() - createFileLabel.getWidthWithPadding() - 10,
                stage.getHeight() - createFileLabel.getHeightWithPadding() - 10);

        //TODO: remove this workaround
        refreshList();
//        updateScrollPaneSize();
    }

    private void updateScrollPaneSize() {
        for (Actor actor : verticalGroup.getChildren()) {
            ((HorizontalGroup) actor).layout();
            ((HorizontalGroup) actor).pack();
        }
        verticalGroup.pack();
        verticalGroup.layout();

        scrollPane.setWidth(verticalGroup.getWidth());
        scrollPane.setHeight(stage.getHeight());
//        scrollPane.layout();
//        scrollPane.pack();
        scrollPane.setPosition(
                stage.getWidth() / 2 - scrollPane.getWidth() / 2, stage.getHeight() - scrollPane.getHeight());
        scrollPane.layout();
//        scrollPane.pack();
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

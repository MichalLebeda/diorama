package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.ui.BackgroundLabel;
import cz.shroomware.diorama.ui.DFButton;
import cz.shroomware.diorama.ui.DFLabel;
import cz.shroomware.diorama.ui.FilenameDialog;

public class ProjectSelectionScreen implements Screen {
    final VerticalGroup verticalGroup;
    ScrollPane scrollPane;
    BackgroundLabel createFileLabel;
    DioramaGame game;
    Stage stage;
    Color backgroundColor = new Color(0x424242ff);


    public ProjectSelectionScreen(final DioramaGame game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
//        stage.setDebugAll(true);

        verticalGroup = new VerticalGroup();
        verticalGroup.columnAlign(Align.left);
        verticalGroup.align(Align.topLeft);
        verticalGroup.pad(20);
        verticalGroup.space(20);

        scrollPane = new ScrollPane(verticalGroup);

         createFileLabel = new BackgroundLabel(
                "New File",
                game);

        createFileLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FilenameDialog dialog = new FilenameDialog(game, ProjectSelectionScreen.this);
                dialog.show(stage);
                super.clicked(event, x, y);
            }
        });
        stage.addActor(createFileLabel);

        stage.addActor(scrollPane);
    }

    public void refreshList() {
        verticalGroup.clear();
        final Drawable darkBackground = game.getSkin().getDrawable(Utils.DARK_BACKGROUND_DRAWABLE);

        FileHandle[] fileHandles = Gdx.files.external(Utils.PROJECT_FOLDER).list();
        for (final FileHandle fileHandle : fileHandles) {
            final HorizontalGroup horizontalGroup = new HorizontalGroup() {
                @Override
                public float getPrefWidth() {
                    return stage.getWidth() / 2;
                }

                @Override
                public void draw(Batch batch, float parentAlpha) {
                    darkBackground.draw(batch, getX(), getY(), getWidth(), getHeight());
                    super.draw(batch, parentAlpha);
                }
            };
            horizontalGroup.pad(20);
            horizontalGroup.space(20);
            horizontalGroup.setTouchable(Touchable.enabled);
            horizontalGroup.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.openEditor(fileHandle.name());
                }
            });
            final DFButton button = new DFButton("X", game);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // TODO: use custom dialog for DF font support
                    Dialog dialog = new Dialog("Are you sure", game.getSkin()) {
                        @Override
                        protected void result(Object object) {
                            super.result(object);

                            if ((boolean) object) {
                                fileHandle.delete();
                                verticalGroup.removeActor(horizontalGroup);
                            }
                        }
                    };
                    dialog.button("No", false);
                    dialog.button("Yes", true);
                    dialog.show(stage);
                }
            });

            horizontalGroup.addActor(button);
            horizontalGroup.addActor(new DFLabel(fileHandle.name(), game));
            verticalGroup.addActor(horizontalGroup);
        }

        updateScrollPaneSize();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        refreshList();
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
        stage.getViewport().update(width,height,true);

        createFileLabel.setPosition(
                stage.getWidth() - createFileLabel.getWidthWithPadding() - 10,
                stage.getHeight() - createFileLabel.getHeightWithPadding() - 10);

        updateScrollPaneSize();
    }

    private void updateScrollPaneSize(){
        verticalGroup.layout();
        verticalGroup.pack();

        scrollPane.setWidth(verticalGroup.getWidth());
        scrollPane.setHeight(stage.getHeight());
        scrollPane.setPosition(
                stage.getWidth() / 2 - scrollPane.getWidth() / 2, 0);
        scrollPane.layout();

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

package cz.shroomware.diorama.editor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.Project;
import cz.shroomware.diorama.ui.BackgroundLabel;
import cz.shroomware.diorama.ui.DFLabel;
import cz.shroomware.diorama.ui.NewProjectDialog;

public class ProjectSelectionScreen implements Screen {
    private VerticalGroup verticalGroup;
    private ScrollPane scrollPane;
    private BackgroundLabel createProjectLabel;
    private EditorEngineGame game;
    private EditorResources resources;
    private Stage stage;
    private Color backgroundColor = new Color(0x424242ff);
    private FileHandle currentDir;

    public ProjectSelectionScreen(final EditorEngineGame game, final FileHandle startFileHandle) {
        this.currentDir = startFileHandle;

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
        createProjectLabel = new BackgroundLabel(
                resources.getSkin(), resources.getDfShader(), "New Project"
        );

        createProjectLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NewProjectDialog dialog = new NewProjectDialog(resources.getSkin(), resources.getDfShader(), startFileHandle, "project") {
                    @Override
                    public void onAccepted(String name) {
                        Project project = new Project(currentDir, name);
                        game.openLevelSelection(project);
                    }
                };
                dialog.show(stage);
                super.clicked(event, x, y);
            }
        });
        stage.addActor(createProjectLabel);
    }

    public void refreshList() {
        verticalGroup.clear();

        FileHandle[] fileHandles = currentDir.list();

        if (!currentDir.path().equals(currentDir.parent().path())) {
            Item item = new Item(game, currentDir.parent()) {
                @Override
                public void onClick(FileHandle fileHandle) {
                    setCurrentFileHandle(fileHandle);
                }
            };
            item.setText(currentDir.path() + "/..");
            verticalGroup.addActor(item);
        }

        for (final FileHandle fileHandle : fileHandles) {
            if (fileHandle.name().startsWith(".")) {
                continue;
            }

            if (!fileHandle.isDirectory()) {
                continue;
            }

            Item item = new Item(game, fileHandle) {
                @Override
                public void onClick(FileHandle fileHandle) {
                    setCurrentFileHandle(fileHandle);
                }
            };
            verticalGroup.addActor(item);
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

        createProjectLabel.setPosition(
                stage.getWidth() - createProjectLabel.getWidthWithPadding() - 10,
                stage.getHeight() - createProjectLabel.getHeightWithPadding() - 10);

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

    public void setCurrentFileHandle(FileHandle fileHandle) {
        FileHandle[] children = fileHandle.list();

        for (FileHandle child : children) {
            if (child.name().equals(Project.PROJECT_FILE)) {
                Project project = new Project(fileHandle);
                game.openLevelSelection(project);
                return;
            }
        }

        this.currentDir = fileHandle;
        refreshList();
    }

    public abstract static class Item extends HorizontalGroup {
        private Drawable currentBackground;
        private Drawable background;
        private Drawable backgroundPressed;
        private DFLabel label;

        public Item(final EditorEngineGame game, final FileHandle fileHandle) {
            final EditorResources resources = game.getResources();
            final Skin skin = resources.getSkin();

            background = skin.getDrawable(Utils.DARK_BACKGROUND_DRAWABLE);
            backgroundPressed = skin.getDrawable(Utils.DARK_BACKGROUND_PRESSED_DRAWABLE);

            currentBackground = background;

            pad(20);
            space(20);
            setTouchable(Touchable.enabled);
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onClick(fileHandle);
                }
            });
            addListener(new ActorGestureListener() {
                @Override
                public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    currentBackground = backgroundPressed;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    currentBackground = background;
                }
            });

            label = new DFLabel(skin, resources.getDfShader(), fileHandle.name());
            addActor(label);
        }

        public void setText(String text) {
            label.setText(text);
        }

        public abstract void onClick(FileHandle fileHandle);

        @Override
        public float getPrefWidth() {
            return getStage().getWidth() / 2f;
        }

        @Override
        public float getWidth() {
            return getPrefWidth();
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            currentBackground.draw(batch, getX(), getY(), getWidth(), getHeight());
            super.draw(batch, parentAlpha);
        }
    }
}

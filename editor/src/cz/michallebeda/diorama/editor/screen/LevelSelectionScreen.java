package cz.michallebeda.diorama.editor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

import java.util.ArrayList;

import cz.michallebeda.diorama.Utils;
import cz.michallebeda.diorama.editor.EditorEngineGame;
import cz.michallebeda.diorama.editor.EditorResources;
import cz.michallebeda.diorama.editor.ui.BackgroundLabel;
import cz.michallebeda.diorama.editor.ui.DFLabel;
import cz.michallebeda.diorama.editor.ui.IconButton;
import cz.michallebeda.diorama.editor.ui.NewLevelDialog;
import cz.michallebeda.diorama.editor.ui.YesNoDialog;
import cz.michallebeda.diorama.engine.Project;
import cz.michallebeda.diorama.engine.level.MetaLevel;

public class LevelSelectionScreen implements Screen {
    private final VerticalGroup verticalGroup;
    private final ScrollPane scrollPane;
    private final BackgroundLabel createLevelLabel;
    private final BackgroundLabel projectLabel;
    private final EditorEngineGame game;
    private final EditorResources resources;
    private final Stage stage;
    private final Color backgroundColor = new Color(0x303030ff);
    private final IconButton logicEditorButton;
    private final DFLabel infoLabel;
    private Project project;

    public LevelSelectionScreen(final EditorEngineGame game) {
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
        createLevelLabel = new BackgroundLabel(
                resources.getSkin(), resources.getDfShader(), "New Level"
        );

        createLevelLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NewLevelDialog dialog = new NewLevelDialog(resources.getSkin(), resources.getDfShader(), LevelSelectionScreen.this.project, "level_") {
                    @Override
                    public void onAccepted(String name, int width, int height) {
                        game.openEditorWithNewLevel(name, width, height);
                    }
                };
                dialog.show(stage);
                super.clicked(event, x, y);
            }
        });
        createLevelLabel.setVisible(false);
        stage.addActor(createLevelLabel);

        projectLabel = new BackgroundLabel(resources.getSkin(), resources.getDfShader(), "");
        projectLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.openProjectSelection();
            }
        });
        stage.addActor(projectLabel);


        Drawable logicEditorIcon = resources.getSkin().getDrawable(Utils.CONNECT_MODE_ICON_DRAWABLE);
        logicEditorButton = new IconButton(resources.getSkin(), logicEditorIcon);
        logicEditorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.openProjectConnections();
            }
        });
        stage.addActor(logicEditorButton);

        infoLabel = new DFLabel(resources.getSkin(), resources.getDfShader(), "");
        infoLabel.setColor(new Color(0x909090FF));
        stage.addActor(infoLabel);
    }

    public void setProject(Project project) {
        if (project != null) {
            this.project = project;

            createLevelLabel.setVisible(true);
            createLevelLabel.setText("New Level");

            projectLabel.setText(project.getName());
        }
    }

    public void refreshList() {
        if (project == null) {
            createLevelLabel.setVisible(false);
            return;
        }

        verticalGroup.clear();

        ArrayList<MetaLevel> metaLevels = project.getMetaLevelsSorted();
        for (final MetaLevel metaLevel : metaLevels) {
            LevelButtonItem horizontalGroup = new LevelButtonItem(game, project, metaLevel) {
                @Override
                public void onDelete(LevelButtonItem button) {
                    verticalGroup.removeActor(button);
                }
            };
            verticalGroup.addActor(horizontalGroup);
        }

        updateScrollPaneSize();
    }

    @Override
    public void show() {
        Gdx.graphics.setTitle("Project - " + project.getName());
        infoLabel.setText("Last ID: " + project.getIdGenerator().getLastId());
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

        createLevelLabel.setPosition(
                stage.getWidth() - createLevelLabel.getWidthWithPadding() - 10,
                stage.getHeight() - createLevelLabel.getHeightWithPadding() - 10);

        projectLabel.setPosition(
                10,
                stage.getHeight() - projectLabel.getHeightWithPadding() - 10);

        logicEditorButton.setPosition(10, projectLabel.getYWithPadding() - 10 - logicEditorButton.getHeight());

        infoLabel.setPosition(10, 10);
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

    public abstract static class LevelButtonItem extends HorizontalGroup {
        Drawable currentBackground;
        Drawable background;
        Drawable backgroundPressed;

        public LevelButtonItem(final EditorEngineGame game, final Project project, final MetaLevel metaLevel) {
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
                    game.openEditor(metaLevel);
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

            IconButton button = new IconButton(
                    skin.getDrawable("default-round"),
                    skin.getDrawable("default-round-down"),
                    skin.getDrawable("cross"));
            button.setIconSize(40);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    YesNoDialog dialog = new YesNoDialog(skin,
                            resources.getDfShader(),
                            "Are you sure") {
                        @Override
                        public void onAccepted() {
                            project.deleteLevel(metaLevel.getName());
                            onDelete(LevelButtonItem.this);
                        }
                    };
                    dialog.show(getStage());
                }
            });
            addActor(button);
            addActor(new DFLabel(skin, resources.getDfShader(), metaLevel.getName()));
        }

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

        public abstract void onDelete(LevelButtonItem button);
    }
}

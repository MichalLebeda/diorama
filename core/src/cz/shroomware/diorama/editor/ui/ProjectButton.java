package cz.shroomware.diorama.editor.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.ui.DFLabel;
import cz.shroomware.diorama.ui.YesNoDialog;

public abstract class ProjectButton extends HorizontalGroup {
    Drawable currentBackground;
    Drawable background;
    Drawable backgroundPressed;

    public ProjectButton(final EditorEngineGame game, final FileHandle projectFileHandle) {
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
                game.openEditor(projectFileHandle.name());
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
                        projectFileHandle.delete();
                        onDelete(ProjectButton.this);
                    }
                };
                dialog.show(getStage());
            }
        });
        addActor(button);
        addActor(new DFLabel(skin, resources.getDfShader(), projectFileHandle.name()));
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

    public abstract void onDelete(ProjectButton button);
}

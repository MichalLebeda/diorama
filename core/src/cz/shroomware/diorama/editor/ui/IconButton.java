package cz.shroomware.diorama.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import cz.shroomware.diorama.Utils;

public class IconButton extends Actor {
    private static final float DEFAULT_ICON_SIZE = 64;
    private static final float BORDER_WIDTH = 20;
    protected float iconSize = DEFAULT_ICON_SIZE;
    protected Drawable background;
    protected Drawable backgroundPressed;
    protected Drawable currentBackground;
    protected Drawable drawable;

    public IconButton(Skin skin, Drawable drawable) {
        this(skin.getDrawable(Utils.DARK_BACKGROUND_DRAWABLE),
                skin.getDrawable(Utils.DARK_BACKGROUND_PRESSED_DRAWABLE),
                drawable);
    }

    public IconButton(Drawable background, Drawable backgroundPressed, Drawable drawable) {
        this.background = background;
        this.backgroundPressed = backgroundPressed;
        this.drawable = drawable;
        currentBackground = this.background;
        setSize(iconSize + 2 * BORDER_WIDTH, iconSize + 2 * BORDER_WIDTH);

        addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                currentBackground = IconButton.this.backgroundPressed;
                event.cancel();
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                currentBackground = IconButton.this.background;
                event.cancel();
            }
        });
    }

    public void setIconSize(float iconSize) {
        this.iconSize = iconSize;
        setSize(iconSize + 2 * BORDER_WIDTH, iconSize + 2 * BORDER_WIDTH);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        currentBackground.draw(batch,
                getX(),
                getY(),
                getWidth(),
                getHeight());

        drawIcon(batch);
    }

    protected void drawIcon(Batch batch) {
        drawable.draw(batch, getX() + BORDER_WIDTH, getY() + BORDER_WIDTH, iconSize, iconSize);
    }

    public void setDrawable(Drawable drawable) {
        if (drawable != null) {
            this.drawable = drawable;
        }
    }
}

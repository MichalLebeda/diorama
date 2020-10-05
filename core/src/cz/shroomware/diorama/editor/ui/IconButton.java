package cz.shroomware.diorama.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import cz.shroomware.diorama.Utils;

//TODO base indicators on this class
public class IconButton extends Actor {
    private static final float ICON_SIZE = 64;
    private static final float BORDER_WIDTH = 20;
    protected Drawable background;
    protected Drawable backgroundPressed;
    protected Drawable currentBackground;
    protected Drawable drawable;
    protected Skin skin;

    public IconButton(Skin skin, Drawable drawable) {
        this.drawable = drawable;
        this.background = skin.getDrawable(Utils.DARK_BACKGROUND_DRAWABLE);
        this.backgroundPressed = skin.getDrawable(Utils.DARK_BACKGROUND_PRESSED_DRAWABLE);
        this.skin = skin;
        currentBackground = background;
        setSize(ICON_SIZE + 2 * BORDER_WIDTH, ICON_SIZE + 2 * BORDER_WIDTH);

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
        drawable.draw(batch, getX() + BORDER_WIDTH, getY() + BORDER_WIDTH, ICON_SIZE, ICON_SIZE);
    }

    public void setDrawable(Drawable drawable) {
        if (drawable != null) {
            this.drawable = drawable;
        }
    }
}

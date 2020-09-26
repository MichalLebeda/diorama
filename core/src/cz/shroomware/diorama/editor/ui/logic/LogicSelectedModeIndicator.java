package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import cz.shroomware.diorama.Utils;

public class LogicSelectedModeIndicator extends Actor {
    private static final float ICON_SIZE = 64;
    private static final float BORDER_WIDTH = 20;
    protected Drawable background;
    protected LogicEditor logicEditor;
    protected Skin skin;

    public LogicSelectedModeIndicator(final LogicEditor editor, Skin skin) {
        this.logicEditor = editor;
        this.background = skin.getDrawable(Utils.DARK_BACKGROUND_DRAWABLE);
        this.skin = skin;
        setSize(ICON_SIZE + 2 * BORDER_WIDTH, ICON_SIZE + 2 * BORDER_WIDTH);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    editor.setPrevMode();
                } else {
                    editor.setNextMode();
                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        background.draw(batch,
                getX(),
                getY(),
                getWidth(),
                getHeight());


        switch (logicEditor.getMode()) {
            case ADD:
                drawIcon(batch, skin.getDrawable(Utils.ITEM_MODE_ICON_DRAWABLE));
                break;
            case DELETE:
                drawIcon(batch, skin.getDrawable(Utils.DELETE_MODE_ICON_DRAWABLE));
                break;
        }
    }

    protected void drawIcon(Batch batch, Drawable drawable) {
        drawable.draw(batch, getX() + BORDER_WIDTH, getY() + BORDER_WIDTH, ICON_SIZE, ICON_SIZE);
    }
}

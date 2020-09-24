package cz.shroomware.diorama.ui;

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
import cz.shroomware.diorama.editor.Editor;

public class SelectedModeIndicator extends Actor {
    private static final float ICON_SIZE = 64;
    private static final float BORDER_WIDTH = 20;
    protected Drawable background;
    protected Editor editor;
    protected Skin skin;

    public SelectedModeIndicator(final Editor editor, Skin skin) {
        this.editor = editor;
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


        switch (editor.getMode()) {
            case ITEM:
                drawIcon(batch, skin.getDrawable(Utils.ITEM_MODE_ICON_DRAWABLE));
                break;
            case TILE:
                drawIcon(batch, skin.getDrawable(Utils.TILE_MODE_ICON_DRAWABLE));
                break;
            case TILE_BUCKET:
                drawIcon(batch, skin.getDrawable(Utils.TILE_BUCKET_MODE_ICON_DRAWABLE));
                break;
            case DELETE:
                drawIcon(batch, skin.getDrawable(Utils.DELETE_MODE_ICON_DRAWABLE));
                break;
            case ID_ASSIGN:
                drawIcon(batch, skin.getDrawable(Utils.ID_ASSIGN_MODE_ICON_DRAWABLE));
                break;
        }
    }

    protected void drawIcon(Batch batch, Drawable drawable) {
        drawable.draw(batch, getX() + BORDER_WIDTH, getY() + BORDER_WIDTH, ICON_SIZE, ICON_SIZE);
    }
}

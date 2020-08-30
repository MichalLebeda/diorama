package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.Editor;

public class SelectedItemIndicator extends Actor {
    private static final float ITEM_SIZE = 64;
    private static final float BORDER_WIDTH = 20;
    protected TextureRegion region = null;
    protected Drawable background;
    protected Editor editor;
    private Sprite sprite;

    public SelectedItemIndicator(Editor editor, Skin skin) {
        this.editor = editor;
        this.background = skin.getDrawable(Utils.DARK_BACKGROUND_DRAWABLE);
        setSize(ITEM_SIZE + 2 * BORDER_WIDTH, ITEM_SIZE + 2 * BORDER_WIDTH);
        sprite = new Sprite();
    }

    public void setItemRegion(TextureRegion region) {
        this.region = region;
        sprite.setRegion(region);

        if (region.getRegionWidth() > region.getRegionHeight()) {
            sprite.setSize(ITEM_SIZE,
                    (float) region.getRegionHeight() / (float) region.getRegionWidth() * ITEM_SIZE);
        } else {
            sprite.setSize((float) region.getRegionWidth() / (float) region.getRegionHeight() * ITEM_SIZE,
                    ITEM_SIZE);
        }

        sprite.setPosition(getX() + (getWidth() / 2 - sprite.getWidth() / 2),
                getY() + (getHeight() / 2 - sprite.getHeight() / 2));
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();

        sprite.setPosition(getX() + (getWidth() / 2 - sprite.getWidth() / 2),
                getY() + (getHeight() / 2 - sprite.getHeight() / 2));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        TextureRegion selectedPrototypeObjectRegion = editor.getPrototypeObjectRegion();
        if (region != selectedPrototypeObjectRegion) {
            region = selectedPrototypeObjectRegion;
            setItemRegion(region);
        }

        background.draw(batch,
                getX(),
                getY(),
                getWidth(),
                getHeight());
        if (region != null) {
            sprite.draw(batch);
        }
    }
}

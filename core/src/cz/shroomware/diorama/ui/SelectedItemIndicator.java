package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import cz.shroomware.diorama.editor.Editor;

public class SelectedItemIndicator extends Actor {
    private static final float ITEM_SIZE = 100;
    private static final float BORDER_WIDTH = 10;
    protected TextureRegion region = null;
    protected TextureRegion backgroundRegion;
    protected Editor editor;
    private Sprite sprite;

    public SelectedItemIndicator(Editor editor, TextureRegion backgroundRegion) {
        this.editor = editor;
        this.backgroundRegion = backgroundRegion;
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
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        TextureRegion selectedPrototypeObjectRegion = editor.getPrototypeObjectRegion();
        if (region != selectedPrototypeObjectRegion) {
            region = selectedPrototypeObjectRegion;
            setItemRegion(region);
        }

        batch.draw(backgroundRegion,
                getX(),
                getY(),
                getWidth(),
                getHeight());
        if (region != null) {
            sprite.draw(batch);
        }
    }
}

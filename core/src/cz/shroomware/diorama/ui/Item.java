package cz.shroomware.diorama.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.SelectedItemIndicator;

public class Item extends HorizontalGroup {
    static final int ITEM_SIZE = 100;

    public Item(DioramaGame game, final TextureAtlas.AtlasRegion region, final SelectedItemIndicator selectedItemIndicator) {
        space(20);
        DFLabel label = new DFLabel(region.name, game);
        label.setFontScale(0.5f);
        addActor(label);

        Image image = new Image(region);

        image.setSize(ITEM_SIZE, ITEM_SIZE);

        Drawable drawable = image.getDrawable();
        if (region.getRegionWidth() > region.getRegionHeight()) {
            drawable.setMinWidth(ITEM_SIZE);
            drawable.setMinHeight((float) region.getRegionHeight() / (float) region.getRegionHeight() * ITEM_SIZE);
        } else {
            drawable.setMinHeight(ITEM_SIZE);
            drawable.setMinWidth((float) region.getRegionWidth() / (float) region.getRegionHeight() * ITEM_SIZE);

            padLeft((drawable.getMinHeight()-drawable.getMinWidth())/2);
            padRight((drawable.getMinHeight()-drawable.getMinWidth())/2);
        }

        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedItemIndicator.setItemRegion(region);
                event.stop();
            }
        });
        addActor(image);
    }
}

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

import cz.shroomware.diorama.DioramaGame;

public class Item extends HorizontalGroup {
    static final int ITEM_SIZE = 100;

    public Item(DioramaGame game, TextureAtlas.AtlasRegion region) {
        DFLabel label = new DFLabel(region.name, game);
        label.setFontScale(0.5f);
        addActor(label);

        Image image = new Image(region);

        image.setSize(ITEM_SIZE, ITEM_SIZE);
        image.getDrawable().setMinHeight(100);
        image.getDrawable().setMinWidth(100);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                super.clicked(event, x, y);
            }
        });
        addActor(image);
    }
}

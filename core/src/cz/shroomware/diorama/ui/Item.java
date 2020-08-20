package cz.shroomware.diorama.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.GameObjectPrototype;

public class Item extends HorizontalGroup {
    static final int ITEM_SIZE = 100;
    GameObjectPrototype prototype;

    public Item(DioramaGame game, final Editor editor, final GameObjectPrototype prototype) {
        this.prototype = prototype;
        space(20);
        DFLabel label = new DFLabel(prototype.getObjectRegion().name, game);
        label.setFontScale(0.3f);
        addActor(label);

        Image image = new Image(prototype.getObjectRegion());

        image.setSize(ITEM_SIZE, ITEM_SIZE);

        Drawable drawable = image.getDrawable();
        if (prototype.getObjectRegion().getRegionWidth() > prototype.getObjectRegion().getRegionHeight()) {
            drawable.setMinWidth(ITEM_SIZE);
            drawable.setMinHeight((float) prototype.getObjectRegion().getRegionHeight() / (float) prototype.getObjectRegion().getRegionWidth() * ITEM_SIZE);
        } else {
            drawable.setMinHeight(ITEM_SIZE);
            drawable.setMinWidth((float) prototype.getObjectRegion().getRegionWidth() / (float) prototype.getObjectRegion().getRegionHeight() * ITEM_SIZE);

            padLeft((drawable.getMinHeight()-drawable.getMinWidth())/2);
            padRight((drawable.getMinHeight()-drawable.getMinWidth())/2);
        }

        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editor.setCurrentlySelectedPrototype(prototype);
                event.stop();
            }
        });
        addActor(image);
    }

//    public abstract void onPrototypeSelect(GameObjectPrototype prototype);
}

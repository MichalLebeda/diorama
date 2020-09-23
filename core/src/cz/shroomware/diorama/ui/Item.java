package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.prototype.Prototype;

public class Item extends HorizontalGroup {
    static final int ITEM_SIZE = 100;
    Prototype prototype;

    public Item(EditorResources resources, final Editor editor, final Prototype prototype) {
        this.prototype = prototype;
        space(20);
        align(Align.right);
        DFLabel label = new DFLabel(prototype.getName(), resources.getSkin(), resources.getDfShader());
        label.setFontScale(0.3f);
        addActor(label);

        TextureRegion iconRegion = prototype.getIconRegion();
        Image image = new Image(iconRegion);

        image.setSize(ITEM_SIZE, ITEM_SIZE);

        Drawable drawable = image.getDrawable();
        if (iconRegion.getRegionWidth() > iconRegion.getRegionHeight()) {
            drawable.setMinWidth(ITEM_SIZE);
            drawable.setMinHeight((float) iconRegion.getRegionHeight() / (float) iconRegion.getRegionWidth() * ITEM_SIZE);
        } else {
            drawable.setMinHeight(ITEM_SIZE);
            drawable.setMinWidth((float) iconRegion.getRegionWidth() / (float) prototype.getIconRegion().getRegionHeight() * ITEM_SIZE);

//            padLeft((drawable.getMinHeight() - drawable.getMinWidth()) / 2);
            padRight((drawable.getMinHeight() - drawable.getMinWidth()) / 2);
        }

        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (prototype.dependenciesFulfilled()) {
                    editor.setCurrentlySelectedPrototype(prototype);
                }
                event.stop();
            }
        });
        addActor(image);
    }
}

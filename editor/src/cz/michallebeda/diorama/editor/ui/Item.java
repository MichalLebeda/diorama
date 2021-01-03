package cz.michallebeda.diorama.editor.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import cz.michallebeda.diorama.editor.Editor;
import cz.michallebeda.diorama.engine.level.prototype.Prototype;

public class Item extends HorizontalGroup {
    static final int IMAGE_SIZE = 100;
    Prototype prototype;

    public Item(Skin skin, ShaderProgram dfShader, final Editor editor, final Prototype prototype) {
        this.prototype = prototype;
        space(20);
        align(Align.right);
        DFLabel label = new DFLabel(skin, dfShader, prototype.getName());
        label.setFontScale(0.3f);
        addActor(label);

        TextureRegion iconRegion = prototype.getIconRegion();
        if (iconRegion == null) {
            iconRegion = skin.getRegion("cross");
        }
        Image image = new Image(iconRegion);

        image.setSize(IMAGE_SIZE, IMAGE_SIZE);

        Drawable drawable = image.getDrawable();
        if (iconRegion.getRegionWidth() > iconRegion.getRegionHeight()) {
            drawable.setMinWidth(IMAGE_SIZE);
            drawable.setMinHeight((float) iconRegion.getRegionHeight() / (float) iconRegion.getRegionWidth() * IMAGE_SIZE);
        } else {
            drawable.setMinHeight(IMAGE_SIZE);
            drawable.setMinWidth((float) iconRegion.getRegionWidth() / (float) iconRegion.getRegionHeight() * IMAGE_SIZE);

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

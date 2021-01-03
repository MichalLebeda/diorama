package cz.shroomware.diorama.editor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.Editor;

public class SnapIndicator extends IconButton {
    protected Editor editor;
    Drawable onDrawable;
    Drawable offDrawable;

    public SnapIndicator(final Editor editor, Skin skin) {
        super(skin, skin.getDrawable(Utils.SNAP_OFF_ICON_DRAWABLE));
        this.editor = editor;

        onDrawable = skin.getDrawable(Utils.SNAP_ON_ICON_DRAWABLE);
        offDrawable = skin.getDrawable(Utils.SNAP_OFF_ICON_DRAWABLE);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editor.toggleHardSnap();
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (editor.getHardSnap()) {
            setDrawable(onDrawable);
        } else {
            setDrawable(offDrawable);
        }

        super.draw(batch, parentAlpha);
    }
}

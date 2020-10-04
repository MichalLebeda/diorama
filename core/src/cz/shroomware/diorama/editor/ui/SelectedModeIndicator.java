package cz.shroomware.diorama.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import cz.shroomware.diorama.editor.Editor;

public class SelectedModeIndicator extends IconButton {
    protected Editor editor;

    public SelectedModeIndicator(final Editor editor, Skin skin) {
        super(skin, skin.getDrawable(editor.getMode().getIconName()));
        this.editor = editor;

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
        setDrawable(skin.getDrawable(editor.getMode().getIconName()));

        super.draw(batch, parentAlpha);
    }
}

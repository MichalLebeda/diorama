package cz.shroomware.diorama.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import cz.shroomware.diorama.editor.Editor;

public class SelectedModeIndicator extends IconButton {
    protected Editor editor;
    protected Skin skin;

    public SelectedModeIndicator(final Editor editor, Skin skin) {
        super(skin, skin.getDrawable(editor.getMode().getIconName()));
        this.editor = editor;
        this.skin = skin;

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

        addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                if (amount > 0) {
                    editor.setPrevMode();
                } else if (amount < 0) {
                    editor.setNextMode();
                }
                return super.scrolled(event, x, y, amount);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setDrawable(skin.getDrawable(editor.getMode().getIconName()));

        super.draw(batch, parentAlpha);
    }
}

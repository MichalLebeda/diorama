package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import cz.shroomware.diorama.editor.ui.IconButton;

//TODO: inherit from same parent as SelectedModeIndicator
public class LogicSelectedModeIndicator extends IconButton {
    protected LogicEditor logicEditor;
    protected Skin skin;

    public LogicSelectedModeIndicator(final LogicEditor logicEditor, Skin skin) {
        super(skin, skin.getDrawable(logicEditor.getMode().getIconName()));
        this.logicEditor = logicEditor;
        this.skin = skin;

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    logicEditor.setPrevMode();
                } else {
                    logicEditor.setNextMode();
                }
            }
        });

        addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                if (amount > 0) {
                    logicEditor.setPrevMode();
                } else if (amount < 0) {
                    logicEditor.setNextMode();
                }
                return super.scrolled(event, x, y, amount);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setDrawable(skin.getDrawable(logicEditor.getMode().getIconName()));

        super.draw(batch, parentAlpha);
    }
}

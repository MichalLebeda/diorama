package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import cz.shroomware.diorama.editor.ui.IconButton;

public class LogicSelectedModeIndicator extends IconButton {
    protected LogicEditor logicEditor;

    public LogicSelectedModeIndicator(final LogicEditor logicEditor, Skin skin) {
        super(skin, skin.getDrawable(logicEditor.getMode().getIconName()));
        this.logicEditor = logicEditor;

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
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setDrawable(skin.getDrawable(logicEditor.getMode().getIconName()));

        super.draw(batch, parentAlpha);
    }
}

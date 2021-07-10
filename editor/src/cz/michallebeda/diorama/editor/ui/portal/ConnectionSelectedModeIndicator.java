package cz.michallebeda.diorama.editor.ui.portal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import cz.michallebeda.diorama.editor.ui.IconButton;

//TODO: inherit from same parent as SelectedModeIndicator
public class ConnectionSelectedModeIndicator extends IconButton {
    protected ConnectionEditor connectionEditor;
    protected Skin skin;

    public ConnectionSelectedModeIndicator(final ConnectionEditor connectionEditor, Skin skin) {
        super(skin, skin.getDrawable(connectionEditor.getMode().getIconName()));
        this.connectionEditor = connectionEditor;
        this.skin = skin;

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    connectionEditor.setPrevMode();
                } else {
                    connectionEditor.setNextMode();
                }
            }
        });

        addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                if (amount > 0) {
                    connectionEditor.setPrevMode();
                } else if (amount < 0) {
                    connectionEditor.setNextMode();
                }
                return super.scrolled(event, x, y, amount);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setDrawable(skin.getDrawable(connectionEditor.getMode().getIconName()));

        super.draw(batch, parentAlpha);
    }
}

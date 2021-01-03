package cz.shroomware.diorama.editor.ui;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class DFButton extends Button {
    protected DFLabel label;

    public DFButton(Skin skin, ShaderProgram dfShader, String text) {
        super(skin);

        label = new DFLabel(skin, dfShader, text);
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.center);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });

        add(label);
        setSize(getPrefWidth(), getPrefHeight());
    }
}

package cz.shroomware.diorama.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import cz.shroomware.diorama.editor.EditorResources;

public class DFButton extends Button {
    protected DFLabel label;

    public DFButton(EditorResources resources, String text) {
        super(resources.getSkin());

        label = new DFLabel(text, resources.getSkin(), resources.getDfShader());
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

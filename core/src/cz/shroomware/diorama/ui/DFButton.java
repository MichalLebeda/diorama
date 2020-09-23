package cz.shroomware.diorama.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import cz.shroomware.diorama.editor.EditorResources;

public class DFButton extends Stack {

    public DFButton(EditorResources resources, String text) {
        DFLabel label = new DFLabel(text, resources.getSkin(), resources.getDfShader());
        label.setAlignment(Align.center);

        Button button = new Button(resources.getSkin());
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });

        label.setTouchable(Touchable.disabled);
        add(button);
        add(label);

//        label.setFontScale(0.3f);
    }
}

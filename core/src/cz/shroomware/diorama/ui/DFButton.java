package cz.shroomware.diorama.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import cz.shroomware.diorama.DioramaGame;

public class DFButton extends Stack {

    public DFButton(String text, DioramaGame game) {
        DFLabel label = new DFLabel(text, game);
        label.setAlignment(Align.center);

        Button button = new Button(game.getSkin());
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

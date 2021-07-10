package cz.michallebeda.diorama.editor.ui;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LeftToBackgroundLabel extends BackgroundLabel {
    float alignLeftToX;

    public LeftToBackgroundLabel(CharSequence text, Skin skin, ShaderProgram dfShader, float alignLeftToX) {
        super(skin, dfShader, text);

        this.alignLeftToX = alignLeftToX;
        setX(alignLeftToX - getWidthWithPadding());
    }

    @Override
    public void setText(CharSequence newText) {
        super.setText(newText);
        pack();
        setX(alignLeftToX - getWidthWithPadding());
    }

}

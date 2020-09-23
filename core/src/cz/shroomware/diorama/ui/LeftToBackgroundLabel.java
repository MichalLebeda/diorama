package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LeftToBackgroundLabel extends BackgroundLabel {
    float alignLeftToX;

    public LeftToBackgroundLabel(CharSequence text, Skin skin, ShaderProgram dfShader, float alignLeftToX) {
        super(text, skin, dfShader);

        this.alignLeftToX = alignLeftToX;
        setX(alignLeftToX - getWidthWithPadding());
    }

    public LeftToBackgroundLabel(CharSequence text, Skin skin, String styleName, ShaderProgram dfShader, float alignLeftToX) {
        super(text, skin, styleName, dfShader);

        this.alignLeftToX = alignLeftToX;
        setX(alignLeftToX - getWidthWithPadding());
    }

    public LeftToBackgroundLabel(CharSequence text, Skin skin, String fontName, Color color, ShaderProgram dfShader, float alignLeftToX) {
        super(text, skin, fontName, color, dfShader);

        this.alignLeftToX = alignLeftToX;
        setX(alignLeftToX - getWidthWithPadding());
    }

    public LeftToBackgroundLabel(CharSequence text, Skin skin, String fontName, String colorName, ShaderProgram dfShader, float alignLeftToX) {
        super(text, skin, fontName, colorName, dfShader);

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

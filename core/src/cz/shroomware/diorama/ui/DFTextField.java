package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;


public class DFTextField extends TextField {
    ShaderProgram dfShader;

    public DFTextField(String text, Skin skin, ShaderProgram dfShader) {
        super(text, skin);
        this.dfShader = dfShader;
    }

    public DFTextField(String text, Skin skin, String styleName, ShaderProgram dfShader) {
        super(text, skin, styleName);
        this.dfShader = dfShader;
    }

    public DFTextField(String text, TextFieldStyle style, ShaderProgram dfShader) {
        super(text, style);
        this.dfShader = dfShader;
    }

    @Override
    protected void drawText(Batch batch, BitmapFont font, float x, float y) {
        batch.setShader(dfShader);
        super.drawText(batch, font, x, y);
        batch.setShader(null);
    }
}

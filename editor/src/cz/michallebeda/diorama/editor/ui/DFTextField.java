package cz.michallebeda.diorama.editor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;


public class DFTextField extends TextField {
    ShaderProgram dfShader;

    public DFTextField(Skin skin, ShaderProgram dfShader, String text) {
        super(text, skin);
        this.dfShader = dfShader;
    }

    @Override
    protected void drawText(Batch batch, BitmapFont font, float x, float y) {
        batch.setShader(dfShader);
        super.drawText(batch, font, x, y);
        batch.setShader(null);
    }
}

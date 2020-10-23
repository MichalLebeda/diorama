package cz.shroomware.diorama.editor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DFLabel extends Label {
    ShaderProgram dfShader;

    public DFLabel(Skin skin, ShaderProgram dfShader, CharSequence text) {
        super(text, skin);
        this.dfShader = dfShader;
        setFontScale(0.32f);
        pack();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setShader(dfShader);
        super.draw(batch, parentAlpha);
        batch.setShader(null);
    }

    @Override
    public void setText(CharSequence newText) {
        super.setText(newText);
        pack();
    }
}

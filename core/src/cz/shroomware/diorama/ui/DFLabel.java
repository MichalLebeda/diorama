package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DFLabel extends Label {
    ShaderProgram dfShader;

    public DFLabel(CharSequence text, Skin skin, ShaderProgram dfShader) {
        super(text, skin);
        this.dfShader = dfShader;
        setFontScale(0.32f);
        pack();
    }

    public DFLabel(CharSequence text, Skin skin, String styleName, ShaderProgram dfShader) {
        super(text, skin, styleName);
        this.dfShader = dfShader;
        setFontScale(0.32f);
        pack();
    }

    public DFLabel(CharSequence text, Skin skin, String fontName, Color color, ShaderProgram dfShader) {
        super(text, skin, fontName, color);
        this.dfShader = dfShader;
        setFontScale(0.32f);
        pack();
    }

    public DFLabel(CharSequence text, Skin skin, String fontName, String colorName, ShaderProgram dfShader) {
        super(text, skin, fontName, colorName);
        this.dfShader = dfShader;
        setFontScale(0.32f);
        pack();
    }

    public DFLabel(CharSequence text, LabelStyle style, ShaderProgram dfShader) {
        super(text, style);
        this.dfShader = dfShader;
        setFontScale(0.32f);
        pack();
    }

//    public DFLabel(CharSequence text, DioramaGame game) {
//        super(text, game.getEditorResources().getSkin());
//    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setShader(dfShader);
        super.draw(batch, parentAlpha);
        batch.setShader(null);
    }
}

package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import cz.shroomware.diorama.DioramaGame;

public class DFLabel extends Label {
    ShaderProgram dfShader;

    public DFLabel(CharSequence text, DioramaGame game) {
        super(text, game.getSkin());
        dfShader = game.getDFShader();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setShader(dfShader);
        super.draw(batch, parentAlpha);
        batch.setShader(null);
    }
}

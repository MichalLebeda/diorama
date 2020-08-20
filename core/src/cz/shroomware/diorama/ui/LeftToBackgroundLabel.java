package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cz.shroomware.diorama.DioramaGame;

public class LeftToBackgroundLabel extends DFLabel {
    float alignLeftToX;
    TextureRegion textureBackgroundRegion;

    public LeftToBackgroundLabel(CharSequence text,
                                 DioramaGame game,
                                 TextureRegion textureBackgroundRegion,
                                 float alignLeftToX) {
        super(text, game);
        this.textureBackgroundRegion = textureBackgroundRegion;
        this.alignLeftToX = alignLeftToX;
        setFontScale(0.3f);
    }

    @Override
    public void setText(CharSequence newText) {
        super.setText(newText);
        pack();
        setX(alignLeftToX - getWidth());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textureBackgroundRegion, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }
}

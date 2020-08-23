package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cz.shroomware.diorama.DioramaGame;

public class LeftToBackgroundLabel extends BackgroundLabel {
    float alignLeftToX;

    public LeftToBackgroundLabel(CharSequence text,
                                 DioramaGame game,
                                 float alignLeftToX) {
        super(text, game);

        this.textureBackgroundRegion = textureBackgroundRegion;
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

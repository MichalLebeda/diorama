package cz.shroomware.diorama.ui;

import cz.shroomware.diorama.DioramaGame;

public class LeftToBackgroundLabel extends BackgroundLabel {
    float alignLeftToX;

    public LeftToBackgroundLabel(CharSequence text,
                                 DioramaGame game,
                                 float alignLeftToX) {
        super(text, game);

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

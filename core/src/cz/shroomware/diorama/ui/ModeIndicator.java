package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.editor.Editor;

public class ModeIndicator extends LeftToBackgroundLabel {
    Editor editor;

    public ModeIndicator(DioramaGame game, Editor editor , float alignLeftTo) {
        super("error", game, alignLeftTo);
        this.editor = editor;
        setText(getModeText());
    }

    protected String getModeText() {
        switch (editor.getMode()) {
            case PLACE:
                return "Place MODE";
            case DELETE:
                return "Delete MODE";
        }

        return "unhandled MODE";
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        setText(getModeText());
    }
}

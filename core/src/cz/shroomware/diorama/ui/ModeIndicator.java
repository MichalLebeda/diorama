package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.g2d.Batch;

import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.EditorResources;

public class ModeIndicator extends LeftToBackgroundLabel {
    Editor editor;

    public ModeIndicator(EditorResources resources, Editor editor, float alignLeftTo) {
        super("error", resources.getSkin(), resources.getDfShader(), alignLeftTo);
        this.editor = editor;
        setText(getModeText());
    }

    protected String getModeText() {
        switch (editor.getMode()) {
            case ITEM:
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

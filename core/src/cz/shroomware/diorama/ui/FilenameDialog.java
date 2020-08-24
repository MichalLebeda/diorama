package cz.shroomware.diorama.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.editor.Editor;
import cz.shroomware.diorama.editor.ProjectSelectionScreen;

public class FilenameDialog extends Dialog {
    TextField textField;

    public FilenameDialog(final DioramaGame game, final ProjectSelectionScreen projectSelectionScreen) {
        super("", game.getSkin());
        getTitleTable().setVisible(false);
        getContentTable().add(new DFLabel("New Project Name:", game));
        textField = new TextField("", game.getSkin());
        textField.setWidth(300);
        getContentTable().add(textField).fill().expand();

        DFButton cancelButton = new DFButton("Cancel", game);
        DFButton okButton = new DFButton("OK", game);

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (textField.getText().length() != 0) {
                    game.openEditor(textField.getText());
                    hide();
                    remove();
                }
            }
        });

        getButtonTable().add(cancelButton);
        getButtonTable().add(okButton);
    }

    public String getText() {
        return textField.getText();
    }

    @Override
    public float getPrefWidth() {
        return getStage().getWidth() * 0.6f;
    }
}

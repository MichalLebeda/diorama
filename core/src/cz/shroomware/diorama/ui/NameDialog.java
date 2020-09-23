package cz.shroomware.diorama.ui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.editor.EditorResources;

public abstract class NameDialog extends Dialog {
    TextField textField;

    public NameDialog(final DioramaGame game, String info, String initialText) {
        super("", game.getEditorResources().getSkin());
        padTop(64);
        getTitleTable().clearChildren();
        getTitleTable().align(Align.left);
        EditorResources resources = game.getEditorResources();
        getTitleTable().add(new DFLabel(info, resources.getSkin(), resources.getDfShader()));
        textField = new DFTextField(initialText, game.getEditorResources().getSkin(), game.getEditorResources().getDfShader());
        textField.setWidth(150);
        getContentTable().add(textField).fill().expand();
//        getContentTable().padTop(64);

        DFButton cancelButton = new DFButton(resources, "Cancel");
        DFButton okButton = new DFButton(resources, "OK");

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (textField.getText().length() > 0) {
                    onAccepted(textField.getText());
                    hide();
                    // For screen transition when creating new project
                    // TODO: edit this
                    remove();
                }
            }
        });

        getButtonTable().add(cancelButton);
        getButtonTable().add(okButton);
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        stage.setKeyboardFocus(textField);
        return dialog;
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        Dialog dialog = super.show(stage, action);
        stage.setKeyboardFocus(textField);
        return dialog;
    }

    public abstract void onAccepted(String name);

    public String getText() {
        return textField.getText();
    }

    @Override
    public float getPrefWidth() {
        return super.getPrefWidth() + textField.getWidth();
    }
}

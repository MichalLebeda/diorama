package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public abstract class NameDialog extends Dialog {
    TextField textField;

    public NameDialog(Skin skin, ShaderProgram dfShader, String info, String initialText) {
        super("", skin);
        padTop(64);
        getTitleTable().clearChildren();
        getTitleTable().align(Align.left);
        getTitleTable().add(new DFLabel(skin, dfShader, info));
        textField = new DFTextField(skin, dfShader, initialText);
        textField.setWidth(150);
        getContentTable().add(textField).fill().expand();
//        getContentTable().padTop(64);

        DFButton cancelButton = new DFButton(skin, dfShader, "Cancel");
        DFButton okButton = new DFButton(skin, dfShader, "OK");

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
        getButtonTable().add(new Actor()).expandX();
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
        setWidth(300);
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

package cz.michallebeda.diorama.editor.ui;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class YesNoDialog extends Dialog {

    public YesNoDialog(Skin skin, ShaderProgram dfShader, String info) {
        super("", skin);
        padTop(64);
        getTitleTable().clearChildren();
        getTitleTable().add(new DFLabel(skin, dfShader, info));

        DFButton cancelButton = new DFButton(skin, dfShader, "No");
        DFButton okButton = new DFButton(skin, dfShader, "Yes");

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onAccepted();
                hide();
            }
        });

        getButtonTable().add(cancelButton);
        getButtonTable().add(new Actor()).expandX();
        getButtonTable().add(okButton);
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
//        stage.setKeyboardFocus(textField);
        return dialog;
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        Dialog dialog = super.show(stage, action);
//        stage.setKeyboardFocus(textField);
        return dialog;
    }

    public abstract void onAccepted();
}

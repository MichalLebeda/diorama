package cz.shroomware.diorama.ui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.editor.EditorResources;

public abstract class YesNoDialog extends Dialog {

    public YesNoDialog(final DioramaGame game, String info) {
        super("", game.getEditorResources().getSkin());
        padTop(64);
        getTitleTable().clearChildren();
        EditorResources resources = game.getEditorResources();
        getTitleTable().add(new DFLabel(info, resources.getSkin(), resources.getDfShader()));

        DFButton cancelButton = new DFButton(resources, "No");
        DFButton okButton = new DFButton(resources, "Yes");

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

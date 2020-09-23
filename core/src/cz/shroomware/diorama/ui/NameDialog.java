package cz.shroomware.diorama.ui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import cz.shroomware.diorama.DioramaGame;

public abstract class NameDialog extends Dialog {
    TextField textField;

    public NameDialog(final DioramaGame game, String info, String initialText) {
        super("", game.getEditorResources().getSkin());
        getTitleTable().setVisible(false);
        getContentTable().add(new DFLabel(info, game));
        textField = new TextField(initialText, game.getEditorResources().getSkin());
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
                if (textField.getText().length() > 0) {
                    onAccepted(textField.getText());
                    hide();
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
        return getStage().getWidth() * 0.6f;
    }
}

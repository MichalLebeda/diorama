package cz.shroomware.diorama.editor.ui;

import com.badlogic.gdx.Gdx;
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

import cz.shroomware.diorama.engine.Project;

public abstract class NewLevelDialog extends Dialog {
    TextField levelNameField;
    TextField widthField;
    TextField heightField;

    public NewLevelDialog(Skin skin, ShaderProgram dfShader, final Project project, String initialText) {
        super("", skin);
        padTop(64);
        getTitleTable().clearChildren();
        getTitleTable().align(Align.left);
        getTitleTable().add(new DFLabel(skin, dfShader, "New Level Name: "));

        levelNameField = new DFTextField(skin, dfShader, initialText);
        getContentTable().add(levelNameField).fill().colspan(2);

        getContentTable().row();

        widthField = new DFTextField(skin, dfShader, "64");
        getContentTable().add(widthField).fill();

        heightField = new DFTextField(skin, dfShader, "64");
        getContentTable().add(heightField).fill();

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
                String name = levelNameField.getText();

                if (levelNameField.getText().length() > 0) {
                    if (project.levelExists(levelNameField.getText())) {
                        Gdx.app.error("NewLevelDialog", "Level exists");
                        return;
                    }

                    int width;
                    int height;

                    try {
                        width = Integer.parseInt(widthField.getText());
                    } catch (NumberFormatException e) {
                        Gdx.app.error("NewLevelDialog", "Invalid WIDTH");
                        return;
                    }

                    try {
                        height = Integer.parseInt(heightField.getText());
                    } catch (NumberFormatException e) {
                        Gdx.app.error("NewLevelDialog", "Invalid HEIGHT");
                        return;
                    }

                    onAccepted(name, width, height);
                    hide();
                    // For screen transition when creating new project
                    // TODO: edit this
                    remove();
                }
            }
        });

        getButtonTable().padTop(12);
        getButtonTable().add(cancelButton);
        getButtonTable().add(new Actor()).expandX();
        getButtonTable().add(okButton);
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        stage.setKeyboardFocus(levelNameField);
        return dialog;
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        Dialog dialog = super.show(stage, action);
        stage.setKeyboardFocus(levelNameField);
        setWidth(400);
        setY(stage.getHeight() / 2 - getHeight() / 2);
        return dialog;
    }

    public abstract void onAccepted(String name, int width, int height);

    public String getText() {
        return levelNameField.getText();
    }

    @Override
    public float getPrefWidth() {
        return super.getPrefWidth() + levelNameField.getWidth();
    }
}

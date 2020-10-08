package cz.shroomware.diorama.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

public abstract class NewProjectDialog extends Dialog {
    TextField projectNameField;

    public NewProjectDialog(Skin skin, ShaderProgram dfShader, final FileHandle parentFileHandle, String initialText) {
        super("", skin);
        padTop(64);
        getTitleTable().clearChildren();
        getTitleTable().align(Align.left);
        getTitleTable().add(new DFLabel(skin, dfShader, "New Project Name: "));

        projectNameField = new DFTextField(skin, dfShader, initialText);
        getContentTable().add(projectNameField).fill().colspan(2);

        getContentTable().row();

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
                String name = projectNameField.getText();

                if (projectNameField.getText().length() > 0) {
                    FileHandle fileHandle = parentFileHandle.child(name);
                    if (fileHandle.exists()) {
                        Gdx.app.error("NewLevelDialog", "Level exists");
                        return;
                    }

                    onAccepted(name);
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
        stage.setKeyboardFocus(projectNameField);
        return dialog;
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        Dialog dialog = super.show(stage, action);
        stage.setKeyboardFocus(projectNameField);
        setWidth(400);
        setY(stage.getHeight() / 2 - getHeight() / 2);
        return dialog;
    }

    public abstract void onAccepted(String name);

    public String getText() {
        return projectNameField.getText();
    }

    @Override
    public float getPrefWidth() {
        return super.getPrefWidth() + projectNameField.getWidth();
    }
}

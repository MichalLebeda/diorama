package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.files.FileHandle;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.logic.Logic;

public class LogicEditor {
    Logic logic;
    FileHandle fileHandle;

    Mode mode = Mode.CONNECT;
    Mode modeBeforeDeleteToggle = mode;

    public LogicEditor(Logic logic, FileHandle fileHandle) {
        this.logic = logic;
        this.fileHandle = fileHandle;
    }

    public Logic getLogic() {
        return logic;
    }

//    public String getLevelName() {
//        return levelName;
//    }


    public FileHandle getLevelFileHandle() {
        return fileHandle;
    }

    public void setNextMode() {
        mode = mode.getNextMode();
    }

    public void setPrevMode() {
        mode = mode.getPrevMode();
    }

    public void toggleDelete() {
        if (mode == Mode.DISCONNECT) {
            mode = modeBeforeDeleteToggle;
        } else {
            modeBeforeDeleteToggle = mode;
            mode = Mode.DISCONNECT;
        }
    }

    public Mode getMode() {
        return mode;
    }

    enum Mode {
        CONNECT(Utils.CONNECT_MODE_ICON_DRAWABLE),
        DISCONNECT(Utils.DISCONNECT_MODE_ICON_DRAWABLE);

        private String iconName;

        Mode(String iconName) {
            this.iconName = iconName;
        }

        public String getIconName() {
            return iconName;
        }

        public Mode getNextMode() {
            return Mode.values()[(this.ordinal() + 1) % (Mode.values().length)];
        }

        public Mode getPrevMode() {
            return Mode.values()[(Mode.values().length + this.ordinal() - 1) % (Mode.values().length)];
        }
    }
}

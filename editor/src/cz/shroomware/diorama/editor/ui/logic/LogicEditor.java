package cz.shroomware.diorama.editor.ui.logic;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.logic.Logic;

public class LogicEditor {
    protected Logic logic;
    protected String name;

    protected Mode mode = Mode.CONNECT;
    protected Mode modeBeforeDeleteToggle = mode;

    public LogicEditor(Logic logic, String name) {
        this.logic = logic;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Logic getLogic() {
        return logic;
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

        private final String iconName;

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

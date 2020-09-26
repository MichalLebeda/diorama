package cz.shroomware.diorama.editor.ui.logic;

import cz.shroomware.diorama.engine.level.logic.Logic;

public class LogicEditor {
    Logic logic;
    String levelName;

    Mode mode = Mode.ADD;
    Mode modeBeforeDeleteToggle = mode;

    public LogicEditor(Logic logic, String levelName) {
        this.logic = logic;
        this.levelName = levelName;
    }

    public Logic getLogic() {
        return logic;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setNextMode() {
        mode = mode.getNextMode();
    }

    public void setPrevMode() {
        mode = mode.getPrevMode();
    }


    public void toggleDelete() {
        if (mode == Mode.DELETE) {
            mode = modeBeforeDeleteToggle;
        } else {
            modeBeforeDeleteToggle = mode;
            mode = Mode.DELETE;
        }
    }

    public Mode getMode() {
        return mode;
    }

    enum Mode {
        ADD, DELETE;

        public Mode getNextMode() {
            return Mode.values()[(this.ordinal() + 1) % (Mode.values().length)];
        }

        public Mode getPrevMode() {
            return Mode.values()[(Mode.values().length + this.ordinal() - 1) % (Mode.values().length)];
        }
    }
}

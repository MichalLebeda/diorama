package cz.shroomware.diorama.engine.level.logic.component;

import com.badlogic.gdx.files.FileHandle;

import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.Handler;

public class LevelSwitcher extends LogicComponent {
    public LevelSwitcher(final EditorEngineGame game, FileHandle[] levels) {
        super(new Identifier("level_switcher"));

        for (final FileHandle levelFileHandle : levels) {
            addHandler(new Handler("open_level_" + levelFileHandle.name()) {
                @Override
                public void handle() {
                    game.openLevel(levelFileHandle);
                }
            });
        }
    }
}

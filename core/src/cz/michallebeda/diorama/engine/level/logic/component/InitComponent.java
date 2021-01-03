package cz.michallebeda.diorama.engine.level.logic.component;

import cz.michallebeda.diorama.Utils;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.Event;

public class InitComponent extends LogicComponent {
    public InitComponent() {
        super(new Identifier(Utils.INIT_ID));

        getIdentifier().setName("Level Started");

        addEvent(new Event("on_start"));
    }
}

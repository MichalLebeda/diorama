package cz.shroomware.diorama.engine.level.logic.component;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.Event;

public class InitComponent extends LogicComponent {
    public InitComponent() {
        super(new Identifier(Utils.INIT_ID));

        getIdentifier().setName("Level Started");

        addEvent(new Event("on_start"));
    }
}

package cz.shroomware.diorama.engine.level.logic.component;

import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.Event;

public class InitComponent extends LogicComponent {
    public InitComponent() {
        super(new Identifier("init"));

        addEvent(new Event("on_init"));
    }
}

package cz.shroomware.diorama.engine.level.logic.component;

import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.Identifiable;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;

public interface LogicComponent extends Identifiable {

    public Array<Event> getEvents();

    public Array<Handler> getHandlers();

    public void onRegister(Logic logic);
}

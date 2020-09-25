package cz.shroomware.diorama.engine.level.logic;

import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.Identifiable;

public interface LogicallyRepresentable extends Identifiable {

    public Array<Event> getEvents();

    public Array<Handler> getHandlers();

    public void onRegister(Logic logic);
}

package cz.shroomware.diorama.engine.level.logic;

import com.badlogic.gdx.utils.Array;

public interface LogicallyRepresentable {

    public Array<Event> getEvents();

    public Array<Handler> getHandlers();

    public void onRegister(Logic logic);
}

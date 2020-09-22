package cz.shroomware.diorama.engine.level.event;

import com.badlogic.gdx.Gdx;
import com.google.common.eventbus.Subscribe;

public class EventListener {

    private static int eventsHandled;

    @Subscribe
    public void stringEvent(String event) {
        Gdx.app.log("EventListener", "String: " + event);
        eventsHandled++;
    }

    @Subscribe
    public void someCustomEvent(WallHitEvent wallHitEvent) {
        Gdx.app.log("EventListener", "WallHitEvent: " + wallHitEvent.toString());
        eventsHandled++;
    }
}

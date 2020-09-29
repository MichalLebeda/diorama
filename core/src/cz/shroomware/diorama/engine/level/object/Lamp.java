package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.prototype.LampPrototype;

public class Lamp extends GameObject {
    protected Array<Handler> handlers = new Array<>();
    protected boolean on = false;
    protected TextureRegion onRegion;
    protected TextureRegion offRegion;

    public Lamp(Vector3 position, Quaternion quaternion, LampPrototype prototype) {
        super(position, quaternion, prototype.getOffRegion(), prototype);
        createHandlers();
        onRegion = prototype.getOnRegion();
        offRegion = prototype.getOffRegion();
    }

    public Lamp(Vector3 position, LampPrototype prototype) {
        super(position, prototype.getOffRegion(), prototype);
        createHandlers();
        onRegion = prototype.getOnRegion();
        offRegion = prototype.getOffRegion();
    }

    protected void createHandlers() {
        handlers.add(new Handler(this, "turn_on") {
            @Override
            public void handle() {
                turnOn(true);
            }
        });
        handlers.add(new Handler(this, "turn_off") {
            @Override
            public void handle() {
                turnOn(false);
            }
        });
    }

    public void turnOn(boolean on) {
        this.on = on;
        decal.setTextureRegion(on ? onRegion : offRegion);
    }

    @Override
    public Array<Event> getEvents() {
        return null;
    }

    @Override
    public Array<Handler> getHandlers() {
        return handlers;
    }

    @Override
    public void onRegister(Logic logic) {

    }
}

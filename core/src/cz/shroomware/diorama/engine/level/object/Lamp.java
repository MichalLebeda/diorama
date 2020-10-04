package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.prototype.LampPrototype;

public class Lamp extends GameObject {
    protected boolean on = false;
    protected TextureRegion onRegion;
    protected TextureRegion offRegion;

    public Lamp(Vector3 position, LampPrototype prototype) {
        super(position, prototype.getOffRegion(), prototype);
        createHandlers();
        onRegion = prototype.getOnRegion();
        offRegion = prototype.getOffRegion();
    }

    protected void createHandlers() {
        logicComponent.addHandler(new Handler("turn_on") {
            @Override
            public void handle() {
                turnOn(true);
            }
        });
        logicComponent.addHandler(new Handler("turn_off") {
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
}

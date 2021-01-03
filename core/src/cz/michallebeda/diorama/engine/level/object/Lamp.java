package cz.michallebeda.diorama.engine.level.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.logic.Handler;
import cz.michallebeda.diorama.engine.level.logic.component.LogicComponent;
import cz.michallebeda.diorama.engine.level.prototype.LampPrototype;

public class Lamp extends GameObject {
    protected boolean on = false;
    protected TextureRegion onRegion;
    protected TextureRegion offRegion;

    public Lamp(Vector3 position, LampPrototype prototype, Identifier identifier) {
        super(position, prototype.getOffRegion(), prototype, identifier);

        logicComponent = new LogicComponent(identifier);
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

        onRegion = prototype.getOnRegion();
        offRegion = prototype.getOffRegion();
    }

    public void turnOn(boolean on) {
        this.on = on;
        decal.setTextureRegion(on ? onRegion : offRegion);
    }
}

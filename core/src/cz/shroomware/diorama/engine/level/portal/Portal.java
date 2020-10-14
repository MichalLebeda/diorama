package cz.shroomware.diorama.engine.level.portal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Portal extends GameObject {
    private PortalConnector portalConnector;
    private MetaPortal metaPortal;
    private boolean ignored = false;

    public Portal(PortalConnector portalConnector,
                  MetaPortal metaPortal,
                  BoxFactory boxFactory,
                  Resources resources) {

        super(new Vector3(metaPortal.getPosition(), 0),
                resources.getObjectAtlas().findRegion("white"),
                null);

        this.portalConnector = portalConnector;
        this.metaPortal = metaPortal;

        decal.setWidth(metaPortal.getWidth());
        decal.setHeight(2);

        decal.setColor(Color.BLACK);

        decal.setZ(decal.getHeight() / 2);

        float x = metaPortal.getPosition().x;
        float y = metaPortal.getPosition().y;

        Body body = boxFactory.addBoxCenter(x, y, metaPortal.getWidth(), metaPortal.getHeight());
        body.getFixtureList().get(0).setSensor(true);

        attachToBody(body);
    }

    public MetaPortal getMetaPortal() {
        return metaPortal;
    }

    public void setIgnored() {
        ignored = true;
    }

    @Override
    public void onContactBegin() {
        if (ignored) {
            return;
        }

        super.onContactBegin();

        portalConnector.goThrough(metaPortal);
    }

    @Override
    public void onContactEnd() {
        ignored = false;

        super.onContactEnd();
    }

    @Override
    public String getName() {
        return "portal";
    }
}

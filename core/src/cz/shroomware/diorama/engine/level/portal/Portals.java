package cz.shroomware.diorama.engine.level.portal;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Collection;
import java.util.HashMap;

import cz.shroomware.diorama.engine.level.MetaLevel;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Portals {
    protected MetaPortals metaPortals;
    protected boolean dirty = false;
    protected PortalConnector portalConnector;
    protected Logic logic;
    protected BoxFactory boxFactory;
    protected Resources resources;
    HashMap<MetaPortal, Portal> metaToPortal = new HashMap<>();

    public Portals(MetaLevel metaLevel,
                   Logic logic,
                   BoxFactory boxFactory,
                   Resources resources) {
        this.portalConnector = metaLevel.getParentProject().getPortalConnector();
        this.logic = logic;
        this.metaPortals = metaLevel.getMetaPortals();
        this.boxFactory = boxFactory;
        this.resources = resources;

        Collection<MetaPortal> values = metaPortals.getValues();
        for (MetaPortal metaPortal : values) {
            add(new Portal(portalConnector, metaPortal, boxFactory, resources));
        }
    }

    public void setIgnored(MetaPortal metaPortal) {
        metaToPortal.get(metaPortal).setIgnored();
    }

    public void drawObjects(MinimalisticDecalBatch decalBatch) {
        for (Portal portal : metaToPortal.values()) {
            portal.update(0);
            portal.drawDecal(decalBatch);
        }
    }

    public void create(float x, float y, float width, float height) {
        MetaPortal metaPortal = metaPortals.create(x, y, width, height);
        Portal portal = new Portal(portalConnector, metaPortal, boxFactory, resources);
        add(portal);
    }

    public void add(Portal portal) {
        dirty = true;
        metaToPortal.put(portal.getMetaPortal(), portal);

        if (portal.hasLogicComponent()) {
            logic.register(portal.getLogicComponent());
        }
    }

    public void remove(Portal portal) {
        dirty = true;

        // Remove portal from level list
        metaToPortal.remove(portal.getMetaPortal());

        if (portal.hasBody()) {
            Body body = portal.getBody();
            World world = body.getWorld();
            world.destroyBody(body);
        }
        // Remove meta portal from level list
        metaPortals.remove(portal.getMetaPortal());
        // Remove portal from connections
        portalConnector.removeConnectionWith(portal.getMetaPortal());
    }

    public boolean isDirty() {
        return dirty;
    }

//    @Override
//    public boolean assignId(GameObject object, String id) {
//        return assignId(object, id, null);
//    }
//
//    @Override
//    public boolean assignId(GameObject object, String id, Messages messages) {
//        if (id == null || id.equals("")) {
//            Gdx.app.error("GameObjects", "ID NOT(!!!) changed:" + id);
//            Gdx.app.error("GameObjects", "Reason: bad ID");
//            if (messages != null) {
//                messages.showMessage("Bad ID");
//            }
//            return false;
//        }
//
//        id = id.replace(" ", "_");
//        id = id.replace(":", "_");
//
//        if (idToObject.containsKey(id)) {
//            Gdx.app.error("GameObjects", "ID NOT(!!!) changed: " + id);
//            Gdx.app.error("GameObjects", "Reason: Duplicate ID: " + id);
//            if (messages != null) {
//                messages.showMessage("Duplicate ID, using old");
//            }
//            return false;
//        } else if (idToObject.containsKey(object.getIdentifier().getIdString())) {
//            dirty = true;
//            idToObject.remove(object.getIdentifier().getIdString());
//            idToObject.put(id, object);
//            String oldId = object.getIdentifier().getIdString();
//            object.getIdentifier().setIdString(id);
//            logic.componentIdChange(object.getLogicComponent(), oldId);
//            if (messages != null) {
//                messages.showMessage("ID Changed");
//            }
//            return true;
//        } else {
//            dirty = true;
//            idToObject.put(id, object);
//            String oldId = object.getIdentifier().getIdString();
//            object.getIdentifier().setIdString(id);
//            logic.componentIdChange(object.getLogicComponent(), oldId);
//            if (messages != null) {
//                messages.showMessage("New ID assigned");
//            }
//            return true;
//        }
//    }

    public int getSize() {
        return metaToPortal.size();
    }

    public Collection<Portal> getPortals() {
        return metaToPortal.values();
    }
}

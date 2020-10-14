package cz.shroomware.diorama.engine.level.portal;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Collection;
import java.util.HashMap;

import cz.shroomware.diorama.engine.level.MetaLevel;
import cz.shroomware.diorama.engine.level.Resources;
import cz.shroomware.diorama.engine.level.object.GameObject;
import cz.shroomware.diorama.engine.physics.BoxFactory;

public class Portals {
    protected MetaPortals metaPortals;
    protected Array<Portal> portals = new Array<>();
    protected boolean dirty = false;
    protected PortalConnector portalConnector;
    protected BoxFactory boxFactory;
    protected Resources resources;
    HashMap<String, Portal> idToObject = new HashMap<>();

    public Portals(MetaLevel metaLevel,
                   BoxFactory boxFactory,
                   Resources resources) {
        this.portalConnector = metaLevel.getParentProject().getPortalConnector();
        this.metaPortals = metaLevel.getMetaPortals();
        this.boxFactory = boxFactory;
        this.resources = resources;

        Collection<MetaPortal> values = metaPortals.getValues();
        for (MetaPortal metaPortal : values) {
            add(new Portal(portalConnector, metaPortal, boxFactory, resources));
        }
    }

    public void setIgnored(MetaPortal metaPortal) {
        for (int i = 0; i < portals.size; i++) {
            Portal portal = portals.get(i);
            if (metaPortal == portal.getMetaPortal()) {
                portal.setIgnored();
            }
        }
    }

    public void drawObjects(MinimalisticDecalBatch decalBatch) {
        for (GameObject object : portals) {
            object.drawDecal(decalBatch);
        }
    }

    public void create(float x, float y, float width, float height, String id) {
        dirty = true;
        MetaPortal metaPortal = metaPortals.create(x, y, width, height, id);
        portals.add(new Portal(portalConnector, metaPortal, boxFactory, resources));
    }

    public void add(Portal gameObject) {
        dirty = true;
        portals.add(gameObject);
    }

    public void remove(Portal portal) {
        dirty = true;
        portals.removeValue(portal, false);
        idToObject.remove(portal.getIdentifier().getIdString());
        if (portal.hasBody()) {
            Body body = portal.getBody();
            World world = body.getWorld();
            world.destroyBody(body);
        }

        // Remove portal from level list
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
        return portals.size;
    }

    public GameObject get(int i) {
        return portals.get(i);
    }

    public void setDirty() {
        this.dirty = true;
    }
}

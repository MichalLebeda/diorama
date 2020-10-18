package cz.shroomware.diorama.engine.level.portal;

import com.badlogic.gdx.Gdx;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import cz.shroomware.diorama.engine.EngineGame;
import cz.shroomware.diorama.engine.Project;
import cz.shroomware.diorama.engine.level.MetaLevel;

public class PortalConnector {
    protected EngineGame game;
    protected BiMap<MetaPortal, MetaPortal> connections = HashBiMap.create();

    public PortalConnector(EngineGame game) {
        this.game = game;
    }

    public void addConnection(MetaPortal portalA, MetaPortal portalB) {
        if (portalA == null) {
            Gdx.app.error("PortalConnector", "Cannot connect, portalA is null");
            return;
        }

        if (portalB == null) {
            Gdx.app.error("PortalConnector", "Cannot connect, portalB is null");
            return;
        }

        if (connectionExists(portalA)) {
            Gdx.app.log("PortalConnector", "Have to disconnect the old connection");
            removeConnectionWith(portalA);
        }

        if (connectionExists(portalB)) {
            Gdx.app.log("PortalConnector", "Have to disconnect the old connection");
            removeConnectionWith(portalB);
        }

        connections.put(portalA, portalB);

        Gdx.app.log("PortalConnector", "Connected: "
                + portalA.getParentLevel().getName() + ":" + portalA.getIdentifier()
                + " - " + portalB.getParentLevel().getName() + ":" + portalB.getIdentifier());
    }

    public void save(OutputStream outputStream) throws IOException {
        outputStream.write((connections.size() + "\n").getBytes());

        Set<Map.Entry<MetaPortal, MetaPortal>> entries = connections.entrySet();

        String levelNameA;
        int portalIdA;

        String levelNameB;
        int portalIdB;

        String partA;
        String partB;

        for (Map.Entry<MetaPortal, MetaPortal> entry : entries) {
            MetaPortal portalA = entry.getKey();
            MetaPortal portalB = entry.getValue();

            levelNameA = portalA.getParentLevel().getName();
            portalIdA = portalA.getIdentifier().getId();

            levelNameB = portalB.getParentLevel().getName();
            portalIdB = portalB.getIdentifier().getId();

            partA = levelNameA + ":" + portalIdA;
            partB = levelNameB + ":" + portalIdB;

            // format: levelA:portalIdA levelB:portalIdB
            outputStream.write((partA + " " + partB + "\n").getBytes());
        }
    }

    public void load(Project project, BufferedReader bufferedReader) throws IOException {
        int amount = Integer.parseInt(bufferedReader.readLine());

        String partA;
        String partB;

        String levelNameA;
        int portalIdA;

        String levelNameB;
        int portalIdB;

        String line;
        String[] split;
        for (int i = 0; i < amount; i++) {
            line = bufferedReader.readLine();
            split = line.split(" ");
            partA = split[0];
            partB = split[1];

            split = partA.split(":");
            levelNameA = split[0];
            portalIdA = Integer.parseInt(split[1]);

            split = partB.split(":");
            levelNameB = split[0];
            portalIdB = Integer.parseInt(split[1]);

            MetaLevel metaLevelA = project.getMetaLevel(levelNameA);
            MetaLevel metaLevelB = project.getMetaLevel(levelNameB);

            MetaPortal metaPortalA = metaLevelA.getMetaPortals().getMetaPortal(portalIdA);
            MetaPortal metaPortalB = metaLevelB.getMetaPortals().getMetaPortal(portalIdB);

            addConnection(metaPortalA, metaPortalB);
        }
    }

    public void goThrough(MetaPortal portal) {
        MetaPortal targetPortal = getTargetPortal(portal);

        if (targetPortal != null) {
            game.openLevel(targetPortal);
        }
    }

    public void removeConnectionWith(MetaPortal portal) {
        connections.remove(portal);
        connections.inverse().remove(portal);
    }

    public boolean connectionExists(MetaPortal portal) {
        if (connections.containsValue(portal) || connections.containsKey(portal)) {
            return true;
        }

        return false;
    }

    public MetaPortal getTargetPortal(MetaPortal entryPortal) {
        MetaPortal targetPortal;

        targetPortal = connections.get(entryPortal);
        if (targetPortal != null) {
            return targetPortal;
        }

        targetPortal = connections.inverse().get(entryPortal);
        if (targetPortal != null) {
            return targetPortal;
        }

        return null;
    }

    public Set<Map.Entry<MetaPortal, MetaPortal>> getConnections() {
        return connections.entrySet();
    }
}

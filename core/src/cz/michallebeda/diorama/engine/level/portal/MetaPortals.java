package cz.michallebeda.diorama.engine.level.portal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;

import cz.michallebeda.diorama.engine.IdGenerator;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.level.MetaLevel;

public class MetaPortals {
    protected MetaLevel parentLevel;
    protected boolean dirty = false;
    HashMap<Integer, cz.michallebeda.diorama.engine.level.portal.MetaPortal> idToPortal = new HashMap<>();

    public MetaPortals(MetaLevel parentLevel) {
        this.parentLevel = parentLevel;
    }

    public cz.michallebeda.diorama.engine.level.portal.MetaPortal create(float x, float y, float width, float height) {
        IdGenerator idGenerator = parentLevel.getParentProject().getIdGenerator();
        cz.michallebeda.diorama.engine.level.portal.MetaPortal metaPortal = new cz.michallebeda.diorama.engine.level.portal.MetaPortal(parentLevel, x, y, width, height, idGenerator.generateId());
        add(metaPortal);

        return metaPortal;
    }

    private void add(cz.michallebeda.diorama.engine.level.portal.MetaPortal metaPortal) {
        idToPortal.put(metaPortal.getIdentifier().getId(), metaPortal);

        dirty = true;
    }

    public void remove(cz.michallebeda.diorama.engine.level.portal.MetaPortal metaPortal) {
        idToPortal.remove(metaPortal.getIdentifier().getId());

        dirty = true;
    }

    public void save(OutputStream outputStream) throws IOException {
        Gdx.app.log("Portals", "saved");
        outputStream.write((idToPortal.size() + "\n").getBytes());
        Collection<cz.michallebeda.diorama.engine.level.portal.MetaPortal> portals = idToPortal.values();
        for (cz.michallebeda.diorama.engine.level.portal.MetaPortal metaPortal : portals) {
            outputStream.write((metaPortal.getSaveString() + "\n").getBytes());
        }

        dirty = false;
    }

    public void load(BufferedReader bufferedReader, IdGenerator idGenerator) throws IOException {
        idToPortal.clear();

        String line;
        int portalsAmount = Integer.parseInt(bufferedReader.readLine());
        for (int j = 0; j < portalsAmount; j++) {
            line = bufferedReader.readLine();
            String[] attributes = line.split(" ");

            Vector2 position = new Vector2(
                    Float.parseFloat(attributes[0]),
                    Float.parseFloat(attributes[1]));

            float width = Float.parseFloat(attributes[2]);
            float height = Float.parseFloat(attributes[3]);

            String id = attributes[4];
            Identifier identifier = idGenerator.obtainLoadedIdentifier(id);

            if (attributes.length == 6) {
                String name = attributes[5];
                identifier.setName(name);
            }

            cz.michallebeda.diorama.engine.level.portal.MetaPortal metaPortal = new cz.michallebeda.diorama.engine.level.portal.MetaPortal(parentLevel,
                    position,
                    width,
                    height,
                    identifier);

            add(metaPortal);
        }

        dirty = false;
    }

    public int getSize() {
        return idToPortal.size();
    }

    public Collection<cz.michallebeda.diorama.engine.level.portal.MetaPortal> getValues() {
        return idToPortal.values();
    }

    public MetaPortal getMetaPortal(int id) {
        return idToPortal.get(id);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        this.dirty = true;
    }
}

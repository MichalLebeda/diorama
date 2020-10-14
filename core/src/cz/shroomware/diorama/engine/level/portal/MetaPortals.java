package cz.shroomware.diorama.engine.level.portal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;

import cz.shroomware.diorama.engine.IdGenerator;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.MetaLevel;

public class MetaPortals {
    protected MetaLevel parentLevel;
    protected boolean dirty = false;
    HashMap<Integer, MetaPortal> idToPortal = new HashMap<>();

    public MetaPortals(MetaLevel parentLevel) {
        this.parentLevel = parentLevel;
    }

    public MetaPortal create(float x, float y, float width, float height) {
        IdGenerator idGenerator = parentLevel.getParentProject().getIdGenerator();
        MetaPortal metaPortal = new MetaPortal(parentLevel, x, y, width, height, idGenerator.generateId());
        add(metaPortal);

        return metaPortal;
    }

    private void add(MetaPortal metaPortal) {
        idToPortal.put(metaPortal.getIdentifier().getId(), metaPortal);

        dirty = true;
    }

    public void remove(MetaPortal metaPortal) {
        idToPortal.remove(metaPortal.getIdentifier().getId());

        dirty = true;
    }

    public void save(OutputStream outputStream) throws IOException {
        Gdx.app.log("Portals", "saved");
        outputStream.write((idToPortal.size() + "\n").getBytes());
        Collection<MetaPortal> portals = idToPortal.values();
        for (MetaPortal metaPortal : portals) {
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

            MetaPortal metaPortal = new MetaPortal(parentLevel,
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

    public Collection<MetaPortal> getValues() {
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
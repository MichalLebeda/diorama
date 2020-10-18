package cz.shroomware.diorama.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import cz.shroomware.diorama.engine.level.MetaLevel;
import cz.shroomware.diorama.engine.level.portal.PortalConnector;

public class Project {
    public static final String LEVEL_DIR = "level/";
    public static final String DATA_EXTENSION = ".data";
    public static final String METADATA_EXTENSION = ".metadata";
    public static final String PROJECT_FILE = "project.config";
    public static final String PROJECT_CONNECTION_FILE = "project.portal";
    private PortalConnector portalConnector;
    private String name;
    private FileHandle fileHandle;
    private HashMap<String, MetaLevel> metaLevels = new HashMap<>();
    private EngineGame game;
    private IdGenerator idGenerator;

    public Project(EngineGame engineGame, FileHandle parent, String name) {
        this.name = name;
        this.game = engineGame;

        fileHandle = parent.child(name);

        if (!fileHandle.exists()) {
            Gdx.app.error("Project", fileHandle.toString());
            create();
        }

        portalConnector = new PortalConnector(game);

        load();
    }

    public Project(EngineGame engineGame, FileHandle file) {
        this.fileHandle = file;
        this.name = fileHandle.name();
        this.game = engineGame;

        if (!fileHandle.exists()) {
            Gdx.app.error("Project", fileHandle.toString());
            create();
        }

        portalConnector = new PortalConnector(game);

        load();
    }

    private void load() {
        loadIdGenerator();
        loadConnections();
    }

    private void loadIdGenerator() {
        FileHandle projectConfigHandle = getProjectConfigHandle();
        if (projectConfigHandle.exists()) {
            InputStream inputStream = projectConfigHandle.read();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            try {
                int lastId = Integer.parseInt(bufferedReader.readLine());
                idGenerator = new IdGenerator(lastId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            idGenerator = new IdGenerator(-1);
            Gdx.app.log("Project", "No project file");
        }

    }

    private void loadConnections() {
        String[] levels = getLevelNames();
        for (String levelName : levels) {
            MetaLevel metaLevel = new MetaLevel(this,
                    levelName,
                    getLevelMetadata(levelName),
                    getLevelData(levelName));
            metaLevels.put(levelName, metaLevel);
        }

        FileHandle logicFileHandle = getProjectConnectionHandle();
        if (logicFileHandle.exists()) {
            InputStream inputStream = logicFileHandle.read();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            try {
                portalConnector.load(this, bufferedReader);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Gdx.app.log("Project", "No portal file");
        }
    }

    public void saveConnections() {
        OutputStream outputStream = getProjectConnectionHandle().write(false);
        try {
            portalConnector.save(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void create() {
        fileHandle.mkdirs();

        FileHandle file = getProjectConfigHandle();
        file.writeString("0", false);

        getLevelDirHandle().mkdirs();
    }

    public void saveConfig() {
        if (idGenerator.isDirty()) {
            Gdx.app.log("IdGenerator", "saved, last id: " + idGenerator.getLastId());
            FileHandle file = getProjectConfigHandle();
            file.writeString(idGenerator.getLastId() + "\n", false);
        }
    }


    public String getName() {
        return name;
    }

    public FileHandle getFileHandle() {
        return fileHandle;
    }

    public FileHandle getLevelDirHandle() {
        return fileHandle.child(LEVEL_DIR);
    }

    public FileHandle getProjectConfigHandle() {
        return fileHandle.child(PROJECT_FILE);
    }

    public FileHandle getProjectConnectionHandle() {
        return fileHandle.child(PROJECT_CONNECTION_FILE);
    }

    public String[] getLevelNames() {
        // Find all *.data files
        FileHandle[] handles = getLevelDirHandle().list(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(DATA_EXTENSION);
            }
        });

        // extract name form filename "[name].data"
        String[] levelNames = new String[handles.length];
        for (int i = 0; i < handles.length; i++) {
            levelNames[i] = handles[i].name().replace(DATA_EXTENSION, "");
        }

        Arrays.sort(levelNames);

        return levelNames;
    }

    private FileHandle getLevelData(String levelName) {
        return getLevelDirHandle().child(levelName + DATA_EXTENSION);
    }

    private FileHandle getLevelMetadata(String levelName) {
        return getLevelDirHandle().child(levelName + METADATA_EXTENSION);
    }

    public MetaLevel createNewLevel(String levelName) {
        if (metaLevels.containsKey(levelName)) {
            return null;
        }

        FileHandle dataHandle = getLevelData(levelName);
        if (dataHandle.exists()) {
            return null;
        }

        FileHandle metadataHandle = getLevelMetadata(levelName);
        if (metadataHandle.exists()) {
            return null;
        }

        MetaLevel metaLevel = new MetaLevel(this, levelName, metadataHandle, dataHandle);
        metaLevels.put(metaLevel.getName(), metaLevel);
        return metaLevel;
    }

    public void deleteLevel(String name) {
        if (!metaLevels.containsKey(name)) {
            Gdx.app.error("Project", "No meta level for: " + name);
            return;
        }

        MetaLevel metaLevel = metaLevels.get(name);
        metaLevel.getDataFileHandle().delete();
        metaLevel.getMetadataFileHandle().delete();

        metaLevels.remove(name);
    }

    public boolean levelExists(String name) {
        return metaLevels.containsKey(name);
    }

    public Collection<MetaLevel> getMetaLevels() {
        return metaLevels.values();
    }

    public ArrayList<MetaLevel> getMetaLevelsSorted() {
        ArrayList<MetaLevel> arrayList = new ArrayList<>(metaLevels.values());
        Collections.sort(arrayList, new MetaLevelComparator());

        return arrayList;
    }

    static class MetaLevelComparator implements Comparator<MetaLevel> {
        @Override
        public int compare(MetaLevel metaLevelA, MetaLevel metaLevelB) {
            return metaLevelA.getName().compareToIgnoreCase(metaLevelB.getName());
        }
    }

    public MetaLevel getMetaLevel(String levelName) {
        return metaLevels.get(levelName);
    }

    public PortalConnector getPortalConnector() {
        return portalConnector;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }
}

package cz.shroomware.diorama.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import cz.shroomware.diorama.editor.EditorEngineGame;
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
    private EditorEngineGame game;

    public Project(EditorEngineGame editorEngineGame, FileHandle parent, String name) {
        this.name = name;
        this.game = editorEngineGame;

        fileHandle = parent.child(name);

        if (!fileHandle.exists()) {
            create();
        }

        portalConnector = new PortalConnector(game);

        load();
    }

    public Project(EditorEngineGame editorEngineGame, FileHandle file) {
        this.fileHandle = file;
        this.name = fileHandle.name();
        this.game = editorEngineGame;

        if (!fileHandle.exists()) {
            create();
        }

        portalConnector = new PortalConnector(game);

        load();
    }

    private void load() {
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

    private void create() {
        fileHandle.mkdirs();

        FileHandle file = getProjectConfigHandle();
        file.writeString("0", false);

        getLevelDirHandle().mkdirs();
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
        metaLevels.put(name, metaLevel);
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

    public MetaLevel getMetaLevel(String levelName) {
        return metaLevels.get(levelName);
    }

    public PortalConnector getPortalConnector() {
        return portalConnector;
    }
}

package cz.shroomware.diorama.engine;

import com.badlogic.gdx.files.FileHandle;

public class Project {
    public static final String PROJECT_FILE = "project.pixellab";
    private String name;
    private FileHandle fileHandle;

    public Project(FileHandle parent, String name) {
        this.name = name;
        fileHandle = parent.child(name);

        if (!fileHandle.exists()) {
            create();
        }
    }

    public Project(FileHandle file) {
        fileHandle = file;
        name = fileHandle.name();

        if (!fileHandle.exists()) {
            create();
        }
    }

    private void create() {
        fileHandle.mkdirs();
        FileHandle file = fileHandle.child(PROJECT_FILE);
        file.writeString(name, false);

        getLevelDirHandle().mkdirs();
    }

    public String getName() {
        return name;
    }

    public FileHandle getFileHandle() {
        return fileHandle;
    }

    public FileHandle getLevelDirHandle() {
        return fileHandle.child("level");
    }

    public FileHandle getLevelFileHandle(String levelName) {
        return getLevelDirHandle().child(levelName);
    }
}

package cz.shroomware.diorama.engine;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Project {
    private String name;
    private FileHandle fileHandle;

    public Project(String name) {
        this.name = name;
        fileHandle = getProjectFileHandle(null, name);
    }

    public String getProjectName() {
        return name;
    }

    public FileHandle getFileHandle() {
        return fileHandle;
    }

    public FileHandle getLevelFileHandle(String levelName) {
        return fileHandle.child(levelName);
    }

    private FileHandle getProjectFileHandle(String root, String projectName) {
        Application.ApplicationType type = Gdx.app.getType();

        if (root == null) {
            root = "Documents/PixelLab/";
        }

        FileHandle fileHandle = Gdx.files.external(root + projectName + "/levels/");
        if (!fileHandle.exists()) {
            fileHandle.mkdirs();
        }

        return fileHandle;

//        switch (type){
//            case Desktop:
//                FileHandle fileHandle =  Gdx.files.local("PixelLab/");
//                if(!fileHandle.exists()){
//                    fileHandle.mkdirs();
//                }
//                return fileHandle;
//            case Android:
//                FileHandle fileHandle =  Gdx.files.local("PixelLab/");
//                if(!fileHandle.exists()){
//                    fileHandle.mkdirs();
//                }
//                return fileHandle;
//        }
//        return Gdx.files
    }

}

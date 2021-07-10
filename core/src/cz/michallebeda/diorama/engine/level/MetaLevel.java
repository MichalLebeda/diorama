package cz.michallebeda.diorama.engine.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.michallebeda.diorama.engine.Project;
import cz.michallebeda.diorama.engine.level.portal.MetaPortals;

public class MetaLevel {
    Project parentProject;
    FileHandle metadataFileHandle;
    FileHandle dataFileHandle;
    String name;
    cz.michallebeda.diorama.engine.level.portal.MetaPortals metaPortals;

    public MetaLevel(Project parentProject,
                     String name,
                     FileHandle metadataFileHandle,
                     FileHandle dataFileHandle) {

        this.parentProject = parentProject;
        this.name = name;
        this.metadataFileHandle = metadataFileHandle;
        this.dataFileHandle = dataFileHandle;

        metaPortals = new cz.michallebeda.diorama.engine.level.portal.MetaPortals(this);

        if (metadataFileHandle.exists()) {
            InputStream inputStream = metadataFileHandle.read();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            try {
                metaPortals.load(bufferedReader, parentProject.getIdGenerator());
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
            Gdx.app.log("MetaLevel", "Metadata file doesn't exist");
            Gdx.app.log("MetaLevel", "Creating new metadata file");

            metadataFileHandle.writeString("0", false);
        }
    }

    public String getName() {
        return name;
    }

    public FileHandle getMetadataFileHandle() {
        return metadataFileHandle;
    }

    public FileHandle getDataFileHandle() {
        return dataFileHandle;
    }

    public MetaPortals getMetaPortals() {
        return metaPortals;
    }

    public Project getParentProject() {
        return parentProject;
    }
}

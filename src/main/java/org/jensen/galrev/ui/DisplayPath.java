package org.jensen.galrev.ui;

import org.jensen.galrev.model.entities.ImageFile;
import org.jensen.galrev.model.entities.RepositoryDir;

import java.nio.file.Path;

/**
 * Created by jensen on 30.06.15.
 */
public class DisplayPath {
    private Path path;
    private ImageFile imageFile;
    private RepositoryDir reposDir;

    public ImageFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public RepositoryDir getReposDir() {
        return reposDir;
    }

    public void setReposDir(RepositoryDir reposDir) {
        this.reposDir = reposDir;
    }

    @Override
    public String toString() {
        return "DisplayPath{" +
                "path=" + path +
                ", imageFile=" + imageFile +
                ", reposDir=" + reposDir +
                '}';
    }
}

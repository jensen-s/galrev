package org.jensen.galrev.ui.uimodel;

import com.google.common.base.MoreObjects;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.jensen.galrev.model.ReviewProvider;
import org.jensen.galrev.model.entities.FileState;
import org.jensen.galrev.model.entities.ImageFile;
import org.jensen.galrev.model.entities.RepositoryDir;

import java.nio.file.Path;

/**
 * Created by jensen on 25.08.16.
 */
public class ReviewTreeEntry {
    private final SimpleStringProperty fileName = new SimpleStringProperty();
    private final SimpleBooleanProperty accepted = new SimpleBooleanProperty();
    private final SimpleBooleanProperty toDelete = new SimpleBooleanProperty();
    private RepositoryDir repositoryDir;
    private Path path;
    private ImageFile imageFile;

    public ReviewTreeEntry(RepositoryDir repositoryDir, Path path) {
        this.repositoryDir = repositoryDir;
        this.path = path;
        if (path != null) {
            fileNameProperty().set(path.getFileName().toString());
        } else {
            fileNameProperty().set(repositoryDir.getPath());
        }
    }

    public ReviewTreeEntry(Path path) {
        this.path = path;
        fileNameProperty().set(path.getFileName().toString());
    }

    public ReviewTreeEntry(Path parentPath, ImageFile imageFile) {
        this.imageFile = imageFile;
        if (parentPath != null) {
            setPath(parentPath.resolve(imageFile.getFilename()));
        }
        fileNameProperty().set(imageFile.getFilename());
        FileState fileState = imageFile.getState();
        applyFileState(fileState);
    }


    private void applyFileState(FileState fileState) {
        if (fileState != null) {
            switch (fileState) {
                case NEW:
                    acceptedProperty().set(false);
                    toDeleteProperty().set(false);
                    break;
                case REVIEWED:
                    acceptedProperty().set(true);
                    toDeleteProperty().set(false);
                    break;
                case MARKED_FOR_DELETION:
                    acceptedProperty().set(false);
                    toDeleteProperty().set(true);
                    break;
                case DELETED:
                    acceptedProperty().set(false);
                    toDeleteProperty().set(true);
                    break;
                case LOST:
                    acceptedProperty().set(false);
                    toDeleteProperty().set(false);
                    break;
            }
        } else {
            acceptedProperty().set(false);
            toDeleteProperty().set(false);
        }
    }

    private void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public String getFileName() {
        return fileName.get();
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public boolean getAccepted() {
        return accepted.get();
    }

    public SimpleBooleanProperty acceptedProperty() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted.set(accepted);
    }

    public boolean getToDelete() {
        return toDelete.get();
    }

    public SimpleBooleanProperty toDeleteProperty() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete.set(toDelete);
    }

    public RepositoryDir getRepositoryDir() {
        return repositoryDir;
    }

    public ImageFile getImageFile() {
        return imageFile;
    }

    public void setFileState(FileState fileState) {
        if (imageFile != null) {
            imageFile.setState(fileState);
            ReviewProvider.getInstance().mergeFile(imageFile);
            applyFileState(fileState);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fileName", fileName)
                .add("accepted", accepted)
                .add("toDelete", toDelete)
                .add("repositoryDir", repositoryDir != null)
                .add("imageFile", imageFile != null)
                .add("path", path != null)
                .toString();
    }
}

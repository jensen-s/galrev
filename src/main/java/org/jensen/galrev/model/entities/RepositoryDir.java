package org.jensen.galrev.model.entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing one directory being part of a ReviewSet.
 * Created by jensen on 06.05.15.
 */
@Entity
@Table(name=DbConstants.TABLE_PREFIX+"dir",
        uniqueConstraints=@UniqueConstraint(columnNames = {"path", "fk_reviewset"}))
@SequenceGenerator(name = RepositoryDir.SEQ_ID, sequenceName = RepositoryDir.SEQ_NAME)
public class RepositoryDir {
    protected static final String SEQ_NAME = DbConstants.SEQ_PREFIX + "dir_id";
    protected static final String SEQ_ID = DbConstants.SEQ_PREFIX + "RepositoryDir";

    private long id;
    private String path;
    
    private List<ImageFile> files;
    private final Logger logger = LogManager.getLogger();

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_ID)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_repos_dir")
    /**
     * Gets the list of contained files.
     */
    public List<ImageFile> getFiles(){
        if (files == null){
            files = new ArrayList<>();
        }
        return files;
    }

    public void setFiles(List<ImageFile> files) {
        this.files = files;
    }


    public ImageFile addFile(String name){
        logger.debug("add file " + name);
        ImageFile newFile = new ImageFile();
        newFile.setState(FileState.NEW);
        newFile.setFilename(name);
        getFiles().add(newFile);
        return newFile;
    }

    public void removeFile(ImageFile file) {
        getFiles().remove(file);
    }


    @Override
    public String toString() {
        return "RepositoryDir{" +
                "id=" + id +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepositoryDir that = (RepositoryDir) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
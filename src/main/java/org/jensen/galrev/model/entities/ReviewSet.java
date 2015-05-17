package org.jensen.galrev.model.entities;

import javax.persistence.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing one review set, i.e. a set of directories containing files which have a review state.
 * Created by jensen on 09.04.15.
 */
@Entity
@Table(name=DbConstants.TABLE_PREFIX+"review_set")
@SequenceGenerator(name = ReviewSet.SEQ_ID, sequenceName = ReviewSet.SEQ_NAME)
public class ReviewSet {

    protected static final String SEQ_NAME = DbConstants.SEQ_PREFIX+"review_set_id";
    protected static final String SEQ_ID = DbConstants.SEQ_PREFIX+"ReviewSet";

    private String name;
    private long id;
    private List<RepositoryDir> directories;

    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator=SEQ_ID)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_reviewset")
    /**
     * Gets the list of contained directories. The directory list is the complete list of all directories (i.e. also containing
     * subdirectories)
     */
    public List<RepositoryDir> getDirectories(){
        if (directories == null){
            directories = new ArrayList<>();
        }
        return directories;
    }

    public void setDirectories(List<RepositoryDir> directories) {
        this.directories = directories;
    }

    public RepositoryDir addDirectory(Path path){

        RepositoryDir newDir = new RepositoryDir();
        newDir.setPath(path.toString());
        getDirectories().add(newDir);
        return newDir;
    }

    public void removeDirectory(RepositoryDir foundDir) {
        getDirectories().remove(foundDir);
    }

    @Override
    public String toString() {
        return "ReviewSet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReviewSet reviewSet = (ReviewSet) o;

        return id == reviewSet.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}

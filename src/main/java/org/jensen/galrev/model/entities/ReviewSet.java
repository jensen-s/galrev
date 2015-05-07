package org.jensen.galrev.model.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by jensen on 09.04.15.
 */
@Entity
@Table(name="review_set")
public class ReviewSet {
    private String name;
    private long id;
    private List<RepositoryDir> directories;

    @Id
    @Column(name="id")
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
    @JoinColumn(name = "fk_image")
    public List<RepositoryDir> getDirectories(){
        return directories;
    }

    public void setDirectories(List<RepositoryDir> directories) {
        this.directories = directories;
    }
}

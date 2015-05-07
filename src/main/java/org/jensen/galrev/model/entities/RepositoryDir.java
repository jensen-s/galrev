package org.jensen.galrev.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by jensen on 06.05.15.
 */
@Entity
@Table(name="repos_dir")
public class RepositoryDir {
    private int id;

    @Id
    @Column(name="id")
    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
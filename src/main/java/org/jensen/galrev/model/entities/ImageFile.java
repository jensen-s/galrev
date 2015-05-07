package org.jensen.galrev.model.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by jensen on 09.04.15.
 */
@Entity
@Table(name = "image_file")
public class ImageFile {

    private long id;
    private String filename;
    private FileState state;

    @Id
    @Column(name="id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name="filename")
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Column(name="state")
    @Enumerated(EnumType.STRING)
    public FileState getState() {
        return state;
    }

    public void setState(FileState state) {
        this.state = state;
    }
}

package org.jensen.galrev.model.entities;

import javax.persistence.*;

/**
 * Entity representing one file of a directory being part of an review set
 * Created by jensen on 09.04.15.
 */
@Entity
@Table(name = DbConstants.TABLE_PREFIX+"image_file",
        uniqueConstraints=@UniqueConstraint(columnNames = {"filename", "fk_repos_dir"}))
@SequenceGenerator(name = ImageFile.SEQ_ID, sequenceName = ImageFile.SEQ_NAME)
public class ImageFile {
    protected static final String SEQ_NAME = DbConstants.SEQ_PREFIX + "image_file_id";
    protected static final String SEQ_ID = DbConstants.SEQ_PREFIX + "ImageFile";

    private long id;
    private String filename;
    private FileState state;

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_ID)
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

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    public FileState getState() {
        return state;
    }

    public void setState(FileState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ImageFile{" +
                "filename='" + filename + '\'' +
                ", id=" + id +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageFile imageFile = (ImageFile) o;

        return id == imageFile.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}

package org.jensen.galrev.model.entities;

import javax.persistence.*;

/**
 * Entity representing one configuration entry (realized as key/value pair)
 * Created by jensen on 07.05.15.
 */
@Entity
@Table(name=DbConstants.TABLE_PREFIX+"config_entry")
@SequenceGenerator(name = ConfigurationEntry.SEQ_ID, sequenceName = ConfigurationEntry.SEQ_NAME)
public class ConfigurationEntry {

    protected static final String SEQ_NAME = DbConstants.SEQ_PREFIX+"config_entry_id";
    protected static final String SEQ_ID = DbConstants.SEQ_PREFIX+"ConfigEntry";

    private int id;
    private String key;
    private String value;

    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator=SEQ_ID)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Column(name="value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ConfigurationEntry{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationEntry that = (ConfigurationEntry) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}

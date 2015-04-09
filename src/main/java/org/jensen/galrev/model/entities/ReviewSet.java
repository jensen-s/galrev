package org.jensen.galrev.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by jensen on 09.04.15.
 */
@Entity
@Table(name="review_set")
public class ReviewSet {
    private String name;

    @Column(name="name2")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

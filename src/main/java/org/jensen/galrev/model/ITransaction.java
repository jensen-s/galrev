package org.jensen.galrev.model;

import javax.persistence.EntityManager;

/**
 * Created by jensen on 09.04.15.
 */
public interface ITransaction<T> {
    T evaluate(EntityManager em);
}

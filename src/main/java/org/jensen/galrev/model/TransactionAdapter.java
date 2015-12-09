package org.jensen.galrev.model;

import javax.persistence.EntityManager;

/**
 * Adapter to ease implementation of ITransaction
 * Created by jensen on 09.04.15.
 */
public abstract class TransactionAdapter<T> implements ITransaction<T> {
    public T evaluate(EntityManager em) {
        return null;
    }

    @Override
    public void run(EntityManager em) {

    }
}

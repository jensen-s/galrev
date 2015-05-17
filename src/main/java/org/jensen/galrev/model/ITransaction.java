package org.jensen.galrev.model;

import javax.persistence.EntityManager;

/**
 * Interface to allow JpaAccess clients define and execute queries
 * Created by jensen on 09.04.15.
 */
public interface ITransaction<T> {
    /**
     * Executes transaction returning a result (i.e. a query). Commit will be done by surrounding method
     * @param em entity manager to create query
     * @return query result
     */
    T evaluate(EntityManager em);

    /**
     * Executes transaction. Commit will be done by surrounding method
     * @param em entity manager to create query
     */
    void run(EntityManager em);
}

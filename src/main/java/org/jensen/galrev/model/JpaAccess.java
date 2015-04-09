package org.jensen.galrev.model;

import javax.persistence.*;

/**
 * Created by jensen on 09.04.15.
 */
public class JpaAccess {

    private static final String DEFAULT_PERSISTENCE_UNIT = "galrev";
    private static EntityManagerFactory factory;
    private static String persistenceUnit = DEFAULT_PERSISTENCE_UNIT;

    public static void setPersistenceUnit(String persistenceUnit) {
        JpaAccess.persistenceUnit = persistenceUnit;
    }

    protected static <T> T evaluateTransaction(ITransaction<T> trans){
        EntityManager em = createEntityManager();
        T result = null;
        EntityTransaction trs = em.getTransaction();
        try{
            result = trans.evaluate(em);
        }catch (PersistenceException pe){
            if (trs != null){
                trs.rollback();
            }
        }
        return result;
    }

    private static EntityManager createEntityManager(){
        EntityManagerFactory emf = getFactory();
        return emf.createEntityManager();
    }

    private static EntityManagerFactory getFactory() {
        if (factory == null){
            factory=Persistence.createEntityManagerFactory(persistenceUnit);
        }
        return factory;
    }
}

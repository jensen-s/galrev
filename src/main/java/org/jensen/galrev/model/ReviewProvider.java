package org.jensen.galrev.model;

import org.jensen.galrev.model.entities.ReviewSet;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.jensen.galrev.model.JpaAccess.evaluateTransaction;
import java.util.List;

/**
 * Created by jensen on 09.04.15.
 */
public class ReviewProvider {
    private static ReviewProvider instance = new ReviewProvider();


    public static ReviewProvider getInstance() {
        return instance;
    }


    public List<ReviewSet> getAllReviewSets(){
        return evaluateTransaction(new TransactionAdapter<List<ReviewSet>>(){
            @Override
            public List<ReviewSet> evaluate(EntityManager em) {
                TypedQuery<ReviewSet> query = em.createQuery("select rs from ReviewSet rs", ReviewSet.class);
                return query.getResultList();
            }
        });
    }
}

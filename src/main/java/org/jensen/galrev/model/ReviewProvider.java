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

    public ReviewSet createNewReviewSet(){
        return new ReviewSet();
    }

    public ReviewSet mergeReviewSet(ReviewSet toMerge){
        ReviewSet result = evaluateTransaction(new TransactionAdapter<ReviewSet>(){
            @Override
            public ReviewSet evaluate(EntityManager em) {
                System.out.println("In eval");
                ReviewSet result ;
                if (toMerge.getId() == 0){
                    System.out.println("Persist");
                    em.persist(toMerge);
                    result = toMerge;
                }else{
                    System.out.println("merge");
                    result = em.merge(toMerge);
                }
                return result;
            }
        });
        return result;
    }
}

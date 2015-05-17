package org.jensen.galrev.model;

import org.jensen.galrev.model.entities.ImageFile;
import org.jensen.galrev.model.entities.RepositoryDir;
import org.jensen.galrev.model.entities.ReviewSet;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.jensen.galrev.model.JpaAccess.*;
import java.util.List;

/**
 * Data Access for GalleryReview entities
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

    public List<RepositoryDir> getAllRepositoryDirs(){
        List<RepositoryDir> result = evaluateTransaction(new TransactionAdapter<List<RepositoryDir>>(){
            @Override
            public List<RepositoryDir> evaluate(EntityManager em) {
                TypedQuery<RepositoryDir> q = em.createQuery("Select rd from RepositoryDir rd", RepositoryDir.class);
                return q.getResultList();
            }
        });
        return result;
    }


    public List<RepositoryDir> getUnlinkedRepositoryDirs(){
        List<RepositoryDir> result = evaluateTransaction(new TransactionAdapter<List<RepositoryDir>>(){
            @Override
            public List<RepositoryDir> evaluate(EntityManager em) {
                TypedQuery<RepositoryDir> q = em.createQuery("Select rd from RepositoryDir rd where " +
                        "not exists (select 1 from ReviewSet rs where rd member of rs.directories)", RepositoryDir.class);
                return q.getResultList();
            }
        });
        return result;
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

    public void cleanUnlinked() {
        transaction(new TransactionAdapter<Object>() {
            @Override
            public void run(EntityManager em) {
                List<RepositoryDir> unlinkedDirs = getUnlinkedRepositoryDirs();
                for (RepositoryDir dir : unlinkedDirs) {
                    dir = em.find(RepositoryDir.class, dir.getId());
                    em.remove(dir);
                }
            }
        });
    }

    public ImageFile mergeFile(ImageFile imageFile) {
        return evaluateTransaction(new TransactionAdapter<ImageFile>() {
            @Override
            public ImageFile evaluate(EntityManager em) {
                return em.merge(imageFile);
            }
        });
    }
}

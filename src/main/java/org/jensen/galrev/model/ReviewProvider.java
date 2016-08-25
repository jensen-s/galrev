package org.jensen.galrev.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jensen.galrev.model.entities.FileState;
import org.jensen.galrev.model.entities.ImageFile;
import org.jensen.galrev.model.entities.RepositoryDir;
import org.jensen.galrev.model.entities.ReviewSet;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.nio.file.Path;
import java.util.List;

import static org.jensen.galrev.model.JpaAccess.evaluateTransaction;
import static org.jensen.galrev.model.JpaAccess.transaction;

/**
 * Data Access for GalleryReview entities
 * Created by jensen on 09.04.15.
 */
public class ReviewProvider {
    private static final ReviewProvider instance = new ReviewProvider();
    private final Logger logger = LogManager.getLogger();


    public static ReviewProvider getInstance() {
        return instance;
    }


    public List<ReviewSet> getAllReviewSets(){
        return evaluateTransaction(new TransactionAdapter<List<ReviewSet>>(){
            @Override
            public List<ReviewSet> evaluate(EntityManager em) {
                TypedQuery<ReviewSet> query = em.createQuery("select rs from ReviewSet rs where deletedFlag is null", ReviewSet.class);
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


    public ReviewSet deleteReviewSet(ReviewSet toDelete) {
        toDelete.setValid(false);
        if (toDelete.getId() > 0) {
            toDelete = mergeReviewSet(toDelete);
        }
        return toDelete;
    }

    public ReviewSet mergeReviewSet(ReviewSet toMerge){
        logger.debug("Merge set " + toMerge);
        ReviewSet result = evaluateTransaction(new TransactionAdapter<ReviewSet>(){
            @Override
            public ReviewSet evaluate(EntityManager em) {
                logger.debug("In eval");
                ReviewSet result ;
                if (toMerge.getId() == 0){
                    logger.debug("Persist");
                    em.persist(toMerge);
                    result = toMerge;
                    logger.info("Review set created: " + result);
                }else{
                    logger.debug("merge");
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
        logger.debug("Merge file " + imageFile);
        return evaluateTransaction(new TransactionAdapter<ImageFile>() {
            @Override
            public ImageFile evaluate(EntityManager em) {
                return em.merge(imageFile);
            }
        });
    }

    /**
     * Adds a new Repository directory to the given review set and adds all files to the directory. No check will be done
     * whether the files are actually located below the base directory and whether they are files
     * @param set the review set
     * @param baseDir the base directory to be transformed to a new repository directory
     * @param files the files to be added
     */
    public void addFileList(ReviewSet set, Path baseDir, List<Path> files){
        logger.debug("About to add " + files.size()+ " files to " + set);
        RepositoryDir rd = set.addDirectory(baseDir);
        files.stream().map(f -> rd.addFile(f.toAbsolutePath().toString())).forEach(this::mergeFile);
        mergeReviewSet(set);
    }

    public long getReviewSize(ReviewSet set) {
        String jpql = "Select count(imageFile) from ImageFile imageFile, ReviewSet rs where imageFile Member of "
                + " rs.directories.files and rs = :reviewSet";

        Long result = evaluateTransaction(new TransactionAdapter<Long>() {
            @Override
            public Long evaluate(EntityManager em) {
                TypedQuery<Long> query = em.createQuery(jpql, Long.class);
                query.setParameter("reviewSet", set);
                return query.getSingleResult();
            }
        });
        return result != null ? result : 0;
    }

    public long getReviewOpenCount(ReviewSet set) {
        String jpql = "Select count(imageFile) from ImageFile imageFile, ReviewSet rs where imageFile Member of "
                + " rs.directories.files and rs = :reviewSet and imageFile.state = :state";

        Long result = evaluateTransaction(new TransactionAdapter<Long>() {
            @Override
            public Long evaluate(EntityManager em) {
                TypedQuery<Long> query = em.createQuery(jpql, Long.class);
                query.setParameter("reviewSet", set);
                query.setParameter("state", FileState.NEW);
                return query.getSingleResult();
            }
        });
        return result != null ? result : 0;
    }
}

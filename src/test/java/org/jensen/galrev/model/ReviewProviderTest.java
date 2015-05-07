package org.jensen.galrev.model;

import org.jensen.galrev.model.entities.ReviewSet;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jensen on 09.04.15.
 */
public class ReviewProviderTest {

    @Test
    public void testGetAllReviewSets() throws Exception {
        ReviewProvider.getInstance().getAllReviewSets();
    }

    @Test
    public void testCreateReviewSet(){
        ReviewProvider provider = ReviewProvider.getInstance();
        List<ReviewSet> sets = provider.getAllReviewSets();
        assertEquals(0, sets.size());
        ReviewSet rs = provider.createNewReviewSet();
        rs.setName("My review");
        provider.mergeReviewSet(rs);
        sets = provider.getAllReviewSets();
        assertEquals(1, sets.size());


    }
}
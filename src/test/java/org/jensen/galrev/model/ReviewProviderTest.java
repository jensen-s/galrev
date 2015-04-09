package org.jensen.galrev.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jensen on 09.04.15.
 */
public class ReviewProviderTest {

    @Test
    public void testGetAllReviewSets() throws Exception {
        ReviewProvider.getInstance().getAllReviewSets();
    }
}
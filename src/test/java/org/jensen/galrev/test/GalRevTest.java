package org.jensen.galrev.test;

import org.jensen.galrev.model.JpaAccess;

/**
 * Created by jensen on 10.06.15.
 */
public class GalRevTest {
    protected GalRevTest(){
        JpaAccess.setPersistenceUnit("galrev-test");
    }
}

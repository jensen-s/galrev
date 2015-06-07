package org.jensen.galrev.crawl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by jensen on 21.05.15.
 */
public class FileCrawlerTest extends PhysicalFileTest {
    private static final int MIN_FILES_TOP_DIR = 10;
    private static final int MAX_FILES_PER_DIR = 30;

    private TestResultListener resultListener;
    private String testBaseDir;

    @Before
    public void setUp() throws IOException {
        testBaseDir = super.setUp(MIN_FILES_TOP_DIR, MAX_FILES_PER_DIR);
        resultListener = new TestResultListener();
    }

    @Test
    public void testCrawl() throws Exception {
        long start = System.currentTimeMillis();
        FileCrawler crawler = new FileCrawler(resultListener);
        final int updateFrequency = 13;
        crawler.setUpdateFrequency(updateFrequency);
        crawler.crawl(Paths.get(testBaseDir));
        assertTrue(resultListener.getMaxReported() <= updateFrequency);
        assertThat(resultListener.getTotalFiles().size(), is(totalFiles));
        long duration = System.currentTimeMillis() - start;
        System.out.println("Duration for " + totalFiles+" files: " + duration+" ms");
    }

}
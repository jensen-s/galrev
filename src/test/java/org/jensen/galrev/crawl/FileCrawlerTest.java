package org.jensen.galrev.crawl;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by jensen on 21.05.15.
 */
public class FileCrawlerTest extends PhysicalFileTest {
    private static final int MIN_FILES_TOP_DIR = 10;
    private static final int MAX_FILES_PER_DIR = 30;

    private String testBaseDir;

    @Before
    public void setUp() throws IOException {
        testBaseDir = super.setUp(MIN_FILES_TOP_DIR, MAX_FILES_PER_DIR);
    }

    @Test
    public void testCrawl() throws Exception {
        long start = System.currentTimeMillis();
        FileCrawler crawler = new FileCrawler();
        List<CrawledEntity> resultList = crawler.crawl(Paths.get(testBaseDir));
        assertThat(getFileCount(resultList), is(totalFiles + 1)); // add root dir
        long duration = System.currentTimeMillis() - start;
        System.out.println("Duration for " + totalFiles+" files: " + duration+" ms");
        checkType(resultList);
    }

    private void checkType(List<CrawledEntity> ceList) {
        for (CrawledEntity ce: ceList){
            if (!ceList.isEmpty()){
                assertThat(Files.isDirectory(ce.getPath()), is(true));
            }
        }
    }

    private int getFileCount(List<CrawledEntity> resultList) {
        int total = 0;
        for (CrawledEntity ce: resultList){
            total += 1 + getFileCount(ce.getChildren());
        }
        return total;
    }

}
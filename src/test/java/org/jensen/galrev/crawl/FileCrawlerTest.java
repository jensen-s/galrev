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
public class FileCrawlerTest {

    private String testBaseDir="testData";
    private Random rnd;
    private int totalFiles;
    private TestResultListener resultListener;

    @Before
    public void setUp() throws IOException{
        File baseDir = new File(testBaseDir);
        if (baseDir.exists()){
            deleteBaseDir();
        }
        rnd = new java.util.Random();
        baseDir.mkdir();
        totalFiles = createTestFiles(baseDir.getAbsolutePath(), 10, 20);
        System.out.println("Create total files: " + totalFiles);
        resultListener = new TestResultListener();
    }


    @Test
    public void testCrawl() throws Exception {
        FileCrawler crawler = new FileCrawler(resultListener);
        final int updateFrequency = 13;
        crawler.setUpdateFrequency(updateFrequency);
        crawler.crawl(new File(testBaseDir));
        assertTrue(resultListener.getMaxReported() <= updateFrequency);
        assertThat(resultListener.getTotalFiles().size(), is(totalFiles));
    }

    @After
    public void tearDown() throws IOException {
        deleteBaseDir();
    }

    private int createTestFiles(String path, int minFiles, int maxFiles) throws IOException{
        int files = minFiles + Math.abs(rnd.nextInt()) % (maxFiles - minFiles);
        int totalFiles = files;
        for (int i=0; i < files; i++){
            if (i%2 == 0){
                createFile (new File(path,"file"+i+".jpg"));
            }else{
                File subDir = new File(path, "dir"+i);
                subDir.mkdir();
                totalFiles += createTestFiles(subDir.getAbsolutePath(), 0, files-1);
            }
        }
        return totalFiles;
    }

    private void createFile(File file) throws IOException {
        try(PrintWriter pw = new PrintWriter(new FileWriter(file))){
            pw.append(file.getName());
        }
    }

    private void deleteBaseDir() throws IOException {
        Path directory = Paths.get(testBaseDir);

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

}
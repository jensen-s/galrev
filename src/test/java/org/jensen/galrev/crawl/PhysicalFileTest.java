package org.jensen.galrev.crawl;

import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;

/**
 * Created by jensen on 07.06.15.
 */
public class PhysicalFileTest {
    private String testBaseDir="testData";
    protected int totalFiles;
    private Random rnd;

    public String setUp(int minFilesTopTir, int maxFilesPerDir) throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        File baseDir = new File(tmpDir, testBaseDir);
        testBaseDir = baseDir.getAbsolutePath();
        if (baseDir.exists()){
            deleteBaseDir();
        }
        rnd = new java.util.Random();
        baseDir.mkdir();
        totalFiles = createTestFiles(baseDir.getAbsolutePath(), minFilesTopTir, maxFilesPerDir);
        System.out.println("Create total files: " + totalFiles);
        return testBaseDir;
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

    protected void createFile(File file) throws IOException {
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

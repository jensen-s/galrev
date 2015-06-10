package org.jensen.galrev.model;

import org.jensen.galrev.model.entities.FileState;
import org.jensen.galrev.model.entities.ImageFile;
import org.jensen.galrev.model.entities.RepositoryDir;
import org.jensen.galrev.model.entities.ReviewSet;
import org.jensen.galrev.test.GalRevTest;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Test class for ReviewProvider
 * Created by jensen on 09.04.15.
 */
public class ReviewProviderTest extends GalRevTest {

    private ReviewProvider provider;

    @Before
    public void setUp(){
        provider = ReviewProvider.getInstance();
    }

    @Test
    public void testGetAllReviewSets() throws Exception {
        ReviewProvider.getInstance().getAllReviewSets();
    }

    @Test
    public void testCreateReviewSet(){
        List<ReviewSet> sets = provider.getAllReviewSets();
        assertEquals(0, sets.size());
        ReviewSet rs = provider.createNewReviewSet();
        String name1 = "My review";
        rs.setName(name1);
        provider.mergeReviewSet(rs);
        sets = provider.getAllReviewSets();
        assertEquals(1, sets.size());
        ReviewSet rs2 = provider.createNewReviewSet();
        String name2 = "My review2";
        rs2.setName(name2);
        final String testDirName = "/test/directory";
        Path path = Paths.get(testDirName);
        rs2.addDirectory(path);
        rs2=provider.mergeReviewSet(rs2);
        sets = provider.getAllReviewSets();
        assertEquals(2, sets.size());
        findByName(sets, name1);
        ReviewSet found = findByName(sets, name2);
        assertThat(found.getDirectories().size(), is(1));
        assertThat(found.getDirectories().get(0).getPath(), is(testDirName));
        final String testDirName2 ="/another/test/directory";
        found.addDirectory(Paths.get(testDirName2));
        found = provider.mergeReviewSet(found);
        assertThat(found.getDirectories().size(), is(2));
        findByPath(found.getDirectories(), testDirName);
        RepositoryDir foundDir = findByPath(found.getDirectories(), testDirName2);
        found.removeDirectory(foundDir);
        found=provider.mergeReviewSet(found);
        assertThat(found.getDirectories().size(), is(1));
        assertThat(found.getDirectories().get(0).getPath(), is(testDirName));
        List<RepositoryDir> repositoryDirs = provider.getAllRepositoryDirs();
        assertThat(repositoryDirs.size(), is(2));
        System.out.println(repositoryDirs.toString());
        List<RepositoryDir> unlinked = provider.getUnlinkedRepositoryDirs();
        assertThat(unlinked.size(), is(1));
        findByPath(unlinked, testDirName2);
        provider.cleanUnlinked();
        unlinked = provider.getUnlinkedRepositoryDirs();
        assertThat(unlinked.size(), is(0));
        repositoryDirs = provider.getAllRepositoryDirs();
        assertThat(repositoryDirs.size(), is(1));

    }

    @Test
    public void testImageFiles(){
        ReviewSet set = provider.createNewReviewSet();
        String testReview = "TestReview";
        set.setName(testReview);
        set=provider.mergeReviewSet(set);
        String firstDir = "/first/Directory";
        String secondDir = "/second/Directory";
        RepositoryDir firstRepDir = set.addDirectory(Paths.get(firstDir));
        set.addDirectory(Paths.get(secondDir));
        String file1 = "file1";
        String file2 = "file2";
        String file3 = "file3";
        firstRepDir.addFile(file1);
        firstRepDir.addFile(file2);
        firstRepDir.addFile(file3);
        set=provider.mergeReviewSet(set);

        firstRepDir=findByPath(set.getDirectories(), firstDir);
        List<ImageFile> files = firstRepDir.getFiles();
        assertThat(files.size(), is(3));

        ImageFile if1 = findFileByName(files, file1);
        findFileByName(files, file2);
        findFileByName(files, file3);
        assertThat(if1.getState(), is(FileState.NEW));

        if1.setState(FileState.MARKED_FOR_DELETION);
        provider.mergeFile(if1);
        set=findByName(provider.getAllReviewSets(), testReview);
        RepositoryDir foundDir = findByPath(set.getDirectories(), firstDir);
        ImageFile foundFile = findFileByName(foundDir.getFiles(), file1);
        assertThat(foundFile.getState(), is(FileState.MARKED_FOR_DELETION));

    }

    @Test
    public void testAddFileList(){
        ReviewSet rs = provider.createNewReviewSet();
        rs.setName("Test set");
        provider.mergeReviewSet(rs);
        final String base = "/test/path/";
        Path baseDir = Paths.get(base);
        List<Path> files = new ArrayList<>();
        files.add(Paths.get(base+"file1.fl"));
        files.add(Paths.get(base + "file2.fl"));
        files.add(Paths.get(base + "file3.fl"));
        files.add(Paths.get(base + "file4.fl"));
        files.add(Paths.get(base + "file5.fl"));
        provider.addFileList(rs, baseDir, files);

        ReviewSet rs2 = provider.getAllReviewSets().get(0);
        List<RepositoryDir> dir = rs2.getDirectories();
        assertThat(dir.size(), is(1));
        List<ImageFile> imageFiles = dir.get(0).getFiles();
        assertThat(imageFiles.size(), is(files.size()));

    }

    private ImageFile findFileByName(List<ImageFile> files, String name) {
        List<ImageFile> foundFiles = files.stream().filter(s -> name.equals(s.getFilename())).collect(Collectors.toList());
        assertThat(foundFiles.size(), is(1));
        return foundFiles.get(0);
    }

    private ReviewSet findByName(List<ReviewSet> sets, String name) {
        List<ReviewSet> foundSets = sets.stream().filter(s -> name.equals(s.getName())).collect(Collectors.toList());
        assertThat(foundSets.size(), is(1));
        return foundSets.get(0);
    }


    private RepositoryDir findByPath(List<RepositoryDir> sets, String name) {
        List<RepositoryDir> foundSets = sets.stream().filter(s -> name.equals(s.getPath())).collect(Collectors.toList());
        assertThat(foundSets.size(), is(1));
        return foundSets.get(0);
    }
}
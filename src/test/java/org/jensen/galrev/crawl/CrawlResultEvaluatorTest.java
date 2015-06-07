package org.jensen.galrev.crawl;

import org.jensen.galrev.model.ReviewProvider;
import org.jensen.galrev.model.entities.RepositoryDir;
import org.jensen.galrev.model.entities.ReviewSet;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

/**
 * Created by jensen on 07.06.15.
 */
public class CrawlResultEvaluatorTest extends PhysicalFileTest{
    private static final int MAX_FILES_PER_DIR = 15;
    private static final int MIN_FILES_TOP_DIR = 10;
    private String testBaseDir;

    @Before
    public void setUp() throws IOException {
         testBaseDir = super.setUp(MIN_FILES_TOP_DIR, MAX_FILES_PER_DIR);
        ReviewSet rs = ReviewProvider.getInstance().createNewReviewSet();
        rs.addDirectory(Paths.get(testBaseDir));
    }

    @Test
    public void testCompare() throws Exception {
        final List<Path> allPaths = getPaths();
        final ReviewProvider provider = ReviewProvider.getInstance();
        ReviewSet rs = provider.createNewReviewSet();
        rs.setName("rs");
        provider.mergeReviewSet(rs);
        provider.addFileList(rs, Paths.get(testBaseDir), allPaths);
        RepositoryDir rd = rs.getDirectories().get(0);

        List<Path> deleted = new ArrayList<>();
        List<Path> added = new ArrayList<>();

        Path aDir = null;
        for (Path aPath: Files.newDirectoryStream(Paths.get(testBaseDir))){
            if (Files.isRegularFile(aPath)){
                if (deleted.size() < 3){
                    Files.delete(aPath);
                }
            }else if (aDir == null){
                aDir = aPath;
            }
        }

        for (int i=0; i<4; i++){
            createFile(new File ( aDir.toFile(), "newOne"+i+".txt"));
        }

        List<Path> allPathsAfterUpdated = getPaths();

        CrawlResultEvaluator evaluator = new CrawlResultEvaluator();


        RepositoryDir baseDir = provider.getAllReviewSets().get(0).getDirectories().get(0);
        CrawlEvaluation result = evaluator.compare(baseDir, allPathsAfterUpdated);

        assertNotNull(result);
        assertThat(result.getLostFiles().size(),is(deleted.size()));
        assertThat(result.getNewFiles().size(),is(added.size()));

    }

    private List<Path> getPaths() {
        final List<CrawledEntity> allCEs = new ArrayList<>();
        FileCrawler crawler = new FileCrawler(l -> allCEs.addAll(l));
        crawler.crawl(Paths.get(testBaseDir));
        return allCEs.stream().map(ce -> ce.getPath()).filter(p -> Files.isRegularFile(p)).collect(Collectors.toList());
    }
}
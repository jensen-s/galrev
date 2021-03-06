package org.jensen.galrev.crawl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jensen.galrev.model.entities.RepositoryDir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to compare existing review sets (i.e. list of files and directories) with a new comparison list.
 * Created by jensen on 06.06.15.
 */
public class CrawlResultEvaluator {

    private final Logger logger = LogManager.getLogger();
    /**
     * Compares the files stored in the repository directory with the found files
     *
     * @param baseDir    the base dir containing list of files
     * @param foundFiles the physically found files
     * @return the comparison result
     */
    public CrawlEvaluation compare(RepositoryDir baseDir, List<Path> foundFiles) {
        CrawlEvaluation result = new CrawlEvaluation();
        if (baseDir.getFiles() == null) {
            // start with empty list -> all added
            result.getNewFiles().addAll(foundFiles);
        } else {
            compareLists(result, baseDir.getFiles().stream().map(imf -> Paths.get(imf.getFilename())).collect(Collectors.toList()), foundFiles);
        }
        return result;
    }

    private void compareLists(CrawlEvaluation result, List<Path> reposFiles, List<Path> foundFiles) {
        long startMs = System.currentTimeMillis();
        HashMap<String, Path> reposMap = new HashMap<>();
        reposFiles.stream().forEach(p -> reposMap.put(p.toAbsolutePath().toString(), p));
        HashMap<String, Path> foundMap = new HashMap<>();
        foundFiles.stream().forEach(p -> foundMap.put(p.toAbsolutePath().toString(), p));
        reposFiles.stream().filter(aPath -> !foundMap.containsKey(aPath.toAbsolutePath().toString())).forEach(aPath -> result.getLostFiles().add(aPath));
        foundFiles.stream().filter(aPath -> !reposMap.containsKey(aPath.toAbsolutePath().toString())).forEach(aPath -> result.getNewFiles().add(aPath));
        long durationMs = System.currentTimeMillis() - startMs;
        logger.debug("Evaluation result: " + result+" duration: " + durationMs+" ms");
    }
}

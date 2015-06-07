package org.jensen.galrev.crawl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jensen on 21.05.15.
 */
public class FileCrawler {
    private int updateFrequency;
    private ICrawlResultListener resultListener;
    private List<CrawledEntity> resultPool = new ArrayList<>();
    private Logger logger = LogManager.getLogger();

    public FileCrawler(ICrawlResultListener resultListener){
        this.resultListener = resultListener;
    }

    public void setUpdateFrequency(int updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    /**
     * Crawls for image files in the given directory
     * @param startDirectory The starting directory. Must be - of course - a directory
     */
    public void crawl(Path startDirectory){
        if (Files.isDirectory(startDirectory)){
            try {
                crawlDir(startDirectory);
            } catch (IOException e) {
                logger.error("Error reading files", e);
            }
        }
        if (!resultPool.isEmpty()){
            resultListener.filesLocated(resultPool);
        }
    }

    private void crawlDir(Path dir) throws IOException {
        for (Path aFile: Files.newDirectoryStream(dir)){
            addResult(aFile);
            if (Files.isDirectory(aFile)){
                crawlDir(aFile);
            }
        }
    }

    private void addResult(Path aFile) {
        CrawledEntity entity = new CrawledEntity(aFile);
        resultPool.add(entity);
        if (resultPool.size() == updateFrequency){
            resultListener.filesLocated(resultPool);
            resultPool.clear();
        }
    }


}

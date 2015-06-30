package org.jensen.galrev.crawl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jensen on 21.05.15.
 */
public class FileCrawler {
    private CrawledEntity root;
    private Logger logger = LogManager.getLogger();

    public FileCrawler(){
    }

    /**
     * Crawls for image files in the given directory
     * @param startDirectory The starting directory. Must be - of course - a directory
     */
    public List<CrawledEntity> crawl(Path startDirectory){
        if (Files.isDirectory(startDirectory)){
            try {
                root = crawlDir(startDirectory);
            } catch (IOException e) {
                logger.error("Error reading files", e);
            }
        }
       return Arrays.asList(new CrawledEntity[]{root});
    }

    private CrawledEntity crawlDir(Path dir) throws IOException {
        CrawledEntity ce = new CrawledEntity(dir);
        for (Path aFile: Files.newDirectoryStream(dir)){
            if (Files.isDirectory(aFile)){
                ce.addChild(crawlDir(aFile));
            }else{
                ce.addChild(new CrawledEntity(aFile));
            }
        }
        return ce;
    }




}

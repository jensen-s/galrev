package org.jensen.galrev.crawl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jensen on 21.05.15.
 */
public class FileCrawler {
    private int updateFrequency;
    private ICrawlResultListener resultListener;
    private List<CrawledEntity> resultPool = new ArrayList<>();

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
    public void crawl(File startDirectory){
        if (startDirectory.isDirectory()){
            crawlDir(startDirectory);
        }
        if (!resultPool.isEmpty()){
            resultListener.filesLocated(resultPool);
        }
    }

    private void crawlDir(File dir) {
        for (File aFile: dir.listFiles()){
            addResult(aFile);
            if (aFile.isDirectory()){
                crawlDir(aFile);
            }
        }
    }

    private void addResult(File aFile) {
        CrawledEntity entity = new CrawledEntity(aFile);
        resultPool.add(entity);
        if (resultPool.size() == updateFrequency){
            resultListener.filesLocated(resultPool);
            resultPool.clear();
        }
    }


}

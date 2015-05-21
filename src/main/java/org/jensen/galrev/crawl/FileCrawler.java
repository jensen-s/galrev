package org.jensen.galrev.crawl;

import java.io.File;

/**
 * Created by jensen on 21.05.15.
 */
public class FileCrawler {
    private int updateFrequency;
    private ICrawlResultListener resultListener;

    public FileCrawler(ICrawlResultListener resultListener){
        this.resultListener = resultListener;
    }

    public void setUpdateFrequency(int updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public void crawl(File startDirectory){

    }



}

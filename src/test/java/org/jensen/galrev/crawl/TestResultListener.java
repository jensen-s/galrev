package org.jensen.galrev.crawl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jensen on 21.05.15.
 */
public class TestResultListener implements ICrawlResultListener {
    private List<CrawledEntity> totalFiles = new ArrayList<>();
    private int maxReported;

    @Override
    public void filesLocated(List<CrawledEntity> located) {
        maxReported = Math.max(maxReported, located.size());
        totalFiles.addAll(located);
    }

    public List<CrawledEntity> getTotalFiles() {
        return totalFiles;
    }

    public int getMaxReported() {
        return maxReported;
    }
}

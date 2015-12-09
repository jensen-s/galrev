package org.jensen.galrev.crawl;

import java.util.List;

/**
 * Created by jensen on 21.05.15.
 */
public interface ICrawlResultListener {

    /**
     * Invoked after a number of files has been detected
     * @param located
     */
    void filesLocated(List<CrawledEntity> located);
}

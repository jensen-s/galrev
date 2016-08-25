package org.jensen.galrev.crawl;

import java.util.List;

/**
 * Created by jensen on 21.05.15.
 */
interface ICrawlResultListener {

    /**
     * Invoked after a number of files has been detected
     * @param located the located files
     */
    void filesLocated(List<CrawledEntity> located);
}

package org.jensen.galrev.crawl;

import java.io.File;

/**
 * Created by jensen on 21.05.15.
 */
public class CrawledEntity {
    private File file;

    public CrawledEntity(File file){
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}

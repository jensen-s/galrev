package org.jensen.galrev.crawl;

import java.nio.file.Path;

/**
 * Created by jensen on 21.05.15.
 */
public class CrawledEntity {
    private Path path;

    public CrawledEntity(Path path){
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}

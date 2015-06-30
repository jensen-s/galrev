package org.jensen.galrev.crawl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jensen on 21.05.15.
 */
public class CrawledEntity {
    private Path path;
    private List<CrawledEntity> children = new ArrayList<>();

    public CrawledEntity(Path path){
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
    
    public List<CrawledEntity> getChildren(){
        return children;
    }

    public void addChild(CrawledEntity childEntity){
        children.add(childEntity);
    }
}

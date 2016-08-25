package org.jensen.galrev.crawl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CrawledEntity {
    private final Path path;
    private final List<CrawledEntity> children = new ArrayList<>();

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

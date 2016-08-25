package org.jensen.galrev.crawl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CrawlEvaluation {
    private final List<Path> lostFiles = new ArrayList<>();
    private final List<Path> newFiles = new ArrayList<>();


    public List<Path> getLostFiles() {
        return lostFiles;
    }

    public List<Path> getNewFiles() {
        return newFiles;
    }

    @Override
    public String toString() {
        return "CrawlEvaluation{" +
                "lostFiles=" + lostFiles.size() +
                ", newFiles=" + newFiles.size() +
                '}';
    }
}


package org.jensen.galrev.crawl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jensen on 06.06.15.
 */
public class CrawlEvaluation {
    private List<Path> lostFiles = new ArrayList<>();
    private List<Path> newFiles = new ArrayList<>();


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


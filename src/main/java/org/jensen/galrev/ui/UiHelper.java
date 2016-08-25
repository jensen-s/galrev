package org.jensen.galrev.ui;

import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jensen.galrev.model.entities.ImageFile;
import org.jensen.galrev.model.entities.RepositoryDir;
import org.jensen.galrev.ui.uimodel.ReviewTreeEntry;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UiHelper {

    private static Logger logger = LogManager.getLogger(UiHelper.class);

    /**
     * Adds all repository directories to the given tree root so that they represent the underlying
     * filesystem structure. If the given directory lists is missing intermediate directories, they are returned as path.
     * <p>
     * Example:
     * List:
     * dirA
     * dirA/dirB/dirC
     * <p>
     * In this situation a/b would be returned
     *
     * @param root
     * @param repositoryDirList
     * @return The list of missing paths
     */
    public static List<Path> fillTreeItem(TreeItem<ReviewTreeEntry> root, List<RepositoryDir> repositoryDirList) {
        repositoryDirList.sort(Comparator.comparing(RepositoryDir::getPath));
        return buildTreeFromSortedDirList(root, repositoryDirList);

    }

    private static List<Path> buildTreeFromSortedDirList(TreeItem<ReviewTreeEntry> root, List<RepositoryDir> repositoryDirList) {
        TreeItem<ReviewTreeEntry> currentParent = root;
        List<Path> missingPaths = new ArrayList<>();
        for (RepositoryDir dir : repositoryDirList) {
            logger.debug("Handle dir " + dir + " (currentParent: " + currentParent.getValue() + ")");
            if (currentParent == root) {
                currentParent = addDirAsChildTreeItem(currentParent, dir);
            } else {
                final String parentPath = currentParent.getValue().getPath().toString();
                if (isChildPath(dir, parentPath)) {
                    final String extension = dir.getPath().substring(parentPath.length() + 1);
                    final boolean missingIntermediateDir = extension.contains(File.separator);
                    if (missingIntermediateDir) {
                        currentParent = extractMissingIntermediateDirs(currentParent, missingPaths, parentPath, extension);
                    }
                    currentParent = addDirAsChildTreeItem(currentParent, dir);

                } else {
                    while (currentParent != root) {
                        if (isChildPath(dir, currentParent.getValue().getPath().toString())) {
                            break;
                        }
                        currentParent = currentParent.getParent();
                    }
                    currentParent = addDirAsChildTreeItem(currentParent, dir);
                }
            }
        }
        return missingPaths;
    }

    private static boolean isChildPath(RepositoryDir dir, String parentPath) {
        return dir.getPath().startsWith(parentPath);
    }

    private static TreeItem<ReviewTreeEntry> extractMissingIntermediateDirs(TreeItem<ReviewTreeEntry> currentParent, List<Path> missingPaths, String parentPath, String extension) {
        String[] parts = extension.split(File.separator);
        String missing = parentPath + File.separator;
        for (int i = 0; i < parts.length - 1; i++) {
            missing += parts[i];
            final Path aMissingPath = Paths.get(missing);
            missingPaths.add(aMissingPath);
            missing += File.separator;
            ReviewTreeEntry dp = new ReviewTreeEntry(aMissingPath);
            TreeItem<ReviewTreeEntry> treeItem = new TreeItem<>(dp);
            currentParent.getChildren().add(treeItem);
            currentParent = treeItem;
        }
        return currentParent;
    }

    private static TreeItem<ReviewTreeEntry> addDirAsChildTreeItem(TreeItem<ReviewTreeEntry> currentParent, RepositoryDir dir) {
        ReviewTreeEntry dp = getReviewTreeEntryByDir(dir, Paths.get(dir.getPath()));
        final TreeItem<ReviewTreeEntry> treeItem = new TreeItem<>(dp);
        currentParent.getChildren().add(treeItem);
        currentParent = treeItem;
        dir.getFiles().forEach(imgFile -> addFileChild(treeItem, imgFile));
        return currentParent;
    }

    private static void addFileChild(TreeItem<ReviewTreeEntry> parentTreeItem, ImageFile imgFile) {
        ReviewTreeEntry dp = new ReviewTreeEntry(parentTreeItem.getValue().getPath(), imgFile);
        parentTreeItem.getChildren().add(new TreeItem<>(dp));
    }

    private static ReviewTreeEntry getReviewTreeEntryByDir(RepositoryDir dir, Path path) {
        ReviewTreeEntry dp = new ReviewTreeEntry(dir, path);
        return dp;
    }

}

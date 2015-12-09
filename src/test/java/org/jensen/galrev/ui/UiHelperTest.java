package org.jensen.galrev.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.jensen.galrev.model.entities.RepositoryDir;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by jensen on 08.12.15.
 */
public class UiHelperTest {

    @Test
    public void testFillTreeItemEmpty() throws Exception {
        DisplayPath dp = new DisplayPath();
        TreeItem<DisplayPath> root = new TreeItem<>(dp);
        List<Path> missingPaths = UiHelper.fillTreeItem(root, Collections.emptyList());
        assertNotNull(missingPaths);
        assertTrue(missingPaths.isEmpty());
        assertTrue(root.getChildren().isEmpty());
    }

    @Test
    public void testFillTreeItemDirs() throws Exception {
        DisplayPath dp = new DisplayPath();
        TreeItem<DisplayPath> root = new TreeItem<>(dp);
        List<RepositoryDir> dirList = new ArrayList<>();
        dirList.add(createDir("/a/b"));
        dirList.add(createDir("/a/b/c"));
        dirList.add(createDir("/a/b/c/d"));
        dirList.add(createDir("/a/e/f"));
        dirList.add(createDir("/a/b/d"));
        List<Path> missingPaths = UiHelper.fillTreeItem(root, dirList);
        assertTrue(missingPaths.isEmpty());
        printTree(root, 0);
        assertFalse(root.getChildren().isEmpty());
        assertEquals(2, root.getChildren().size());
        testItemListContent(root.getChildren(), "/a/b", "/a/e/f");
        TreeItem<DisplayPath> subItem = getTreeItemByPath(root, "/a/b");
        testItemListContent(subItem.getChildren(), "/a/b/c", "/a/b/d");
        subItem = getTreeItemByPath(subItem, "/a/b/c");
        testItemListContent(subItem.getChildren(), "/a/b/c/d");
    }

    @Test
    public void testFillTreeItemMissingIntermediateDirs() throws Exception {
        DisplayPath dp = new DisplayPath();
        TreeItem<DisplayPath> root = new TreeItem<>(dp);
        List<RepositoryDir> dirList = new ArrayList<>();
        dirList.add(createDir("/a/"));
        dirList.add(createDir("/a/b/c/d"));
        List<Path> missingPaths = UiHelper.fillTreeItem(root, dirList);
        assertFalse(missingPaths.isEmpty());
        assertEquals("Wrong missing paths: " + missingPaths, 2, missingPaths.size());
        assertTrue(missingPaths.contains(Paths.get("/a/b")));
        assertTrue(missingPaths.contains(Paths.get("/a/b/c")));
        printTree(root, 0);
        assertFalse(root.getChildren().isEmpty());
        assertEquals(1, root.getChildren().size());
        testItemListContent(root.getChildren(), "/a");
        TreeItem<DisplayPath> subItem = getTreeItemByPath(root, "/a");
        testItemListContent(subItem.getChildren(), "/a/b");
        subItem = getTreeItemByPath(subItem, "/a/b");
        testItemListContent(subItem.getChildren(), "/a/b/c");
        subItem = getTreeItemByPath(subItem, "/a/b/c");
        testItemListContent(subItem.getChildren(), "/a/b/c/d");
    }

    private void printTree(TreeItem<DisplayPath> root, int indent) {
        String prefix = "";
        for (int i = 0; i < indent; i++) {
            prefix += " ";
        }
        String dispString;
        DisplayPath dp = root.getValue();
        if (dp.getPath() != null) {
            dispString = dp.getPath().toString();
        } else {
            dispString = "root";
        }
        System.out.println(prefix + dispString);
        root.getChildren().forEach(ti -> printTree(ti, indent + 1));
    }

    private TreeItem<DisplayPath> getTreeItemByPath(TreeItem<DisplayPath> root, String path) {
        return root.getChildren()
                .stream()
                .filter(ti -> ti.getValue().getPath() != null && ti.getValue().getPath().toString().equals(path))
                .findAny().get();
    }

    private void testItemListContent(ObservableList<TreeItem<DisplayPath>> treeItems, String... paths) {
        if (paths == null || paths.length == 0) {
            assertTrue(treeItems.isEmpty());
        } else {
            assertEquals(paths.length, treeItems.size());
            List<String> pathList = Arrays.asList(paths);
            Optional<TreeItem<DisplayPath>> wrongEntry =
                    treeItems.stream()
                            .filter(ti -> !pathList.contains(ti.getValue().getPath().toString()))
                            .findAny();
            assertFalse("Did find this one although not expected: " + wrongEntry, wrongEntry.isPresent());
        }
    }

    private RepositoryDir createDir(String path) {
        RepositoryDir dir = new RepositoryDir();
        dir.setPath(path);
        return dir;
    }

    @Test
    public void testFillTreeItemWithFiles() throws Exception {
        DisplayPath dp = new DisplayPath();
        TreeItem<DisplayPath> root = new TreeItem<>(dp);
        List<RepositoryDir> dirList = new ArrayList<>();
        RepositoryDir dir = createDir("/a/b");
        addFiles(dir, "f1", "f2", "f3");
        dirList.add(dir);
        dir = createDir("/a/b/c");
        addFiles(dir, "f4", "f5");
        dirList.add(dir);

        UiHelper.fillTreeItem(root, dirList);
        printTree(root, 0);
        TreeItem<DisplayPath> subItem = getTreeItemByPath(root, "/a/b");
        testItemFileContent(subItem, "f1", "f2", "f3");
        subItem = getTreeItemByPath(subItem, "/a/b/c");
        testItemFileContent(subItem, "f4", "f5");
    }

    private void testItemFileContent(TreeItem<DisplayPath> subItem, String... filenames) {
        List<DisplayPath> children = subItem.getChildren().stream().map(TreeItem::getValue).collect(Collectors.toList());
        long childFiles = children.stream().filter(dp -> dp.getImageFile() != null).count();
        assertEquals(filenames.length, (int) childFiles);
        List<String> filenameList = Arrays.asList(filenames);
        Optional<DisplayPath> wrongEntry = children.stream()
                .filter(dp -> ((dp.getImageFile() != null) &&
                        (!filenameList.contains(dp.getImageFile().getFilename()))))
                .findAny();
        assertFalse("Did find this one although not expected: " + wrongEntry, wrongEntry.isPresent());
    }

    private void addFiles(RepositoryDir dir, String... files) {
        for (String file : files) {
            dir.addFile(file);
        }
    }


}
package org.jensen.galrev.ui;

import static org.jensen.galrev.ui.translate.Texts.getText;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jensen.galrev.app.GalRev;
import org.jensen.galrev.crawl.CrawledEntity;
import org.jensen.galrev.crawl.FileCrawler;
import org.jensen.galrev.model.ReviewProvider;
import org.jensen.galrev.model.entities.FileState;
import org.jensen.galrev.model.entities.ImageFile;
import org.jensen.galrev.model.entities.RepositoryDir;
import org.jensen.galrev.model.entities.ReviewSet;
import org.jensen.galrev.ui.translate.Texts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * FXML Controller for MainView
 * Created by jensen on 09.04.15.
 */
public class MainView {

    private static final String FXML_FILE_NAME = "mainview.fxml";
    private static final Logger logger = LogManager.getLogger(MainView.class);
    private static final boolean TEST_DATA = false;

    @FXML
    private Text txtCurrentFile;

    @FXML
    private Label lblReviewName;

    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private TreeTableView<DisplayPath> ttvFiles;
    @FXML
    private Button btnPrev;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUndo;
    @FXML
    private Button btnAccept;
    @FXML
    private Button btnNext;
    @FXML
    private TreeTableColumn<DisplayPath, String> colFile;
    @FXML
    private TreeTableColumn<DisplayPath, Boolean> colAccept;
    @FXML
    private TreeTableColumn<DisplayPath, Boolean> colDelete;

    private static ExecutorService executor = Executors.newFixedThreadPool(1);

    private ReviewProvider provider = ReviewProvider.getInstance();

    private StringProperty reviewSetNameProperty = new SimpleStringProperty();
    private SimpleObjectProperty<ReviewSet> reviewSetProperty=new SimpleObjectProperty<>();
    private SimpleObjectProperty<ImageFile> currentFile = new SimpleObjectProperty<>();

    public static Parent load(Stage stage) throws IOException {
        URL fxmlResource = MainView.class.getResource(FXML_FILE_NAME);
        InputStream inputStream = Texts.getBundleStream();
        ResourceBundle bundle = new PropertyResourceBundle(inputStream);
        logger.debug("Resource URL: " + fxmlResource);
        FXMLLoader loader = new FXMLLoader(fxmlResource, bundle);
        final Parent parent = loader.load();
        MainView mv = (MainView)loader.getController();
        mv.initKeyHandling(stage);
        return parent;
    }

    @FXML
    void initialize() {
        Platform.runLater(()-> {
                    progressIndicator.getScene().getWindow().setEventDispatcher((event, chain) -> {
                        try {
                            return chain.dispatchEvent(event);
                        } catch (Exception e) {
                            // TODO: Detailed error
                            DialogHelper.showException(Texts.getText("messageGeneralException"), e);
                            return null;
                        }
                    });
                });
        setButtonImage(btnPrev, UiResources.Images.ARROW_LEFT);
        setButtonImage(btnDelete, UiResources.Images.DELETE);
        setButtonImage(btnUndo, UiResources.Images.UNDO);
        setButtonImage(btnAdd, UiResources.Images.FOLDER_ADD);
        setButtonImage(btnAccept, UiResources.Images.ACCEPT);
        setButtonImage(btnNext, UiResources.Images.ARROW_RIGHT);
        lblReviewName.textProperty().bind(reviewSetNameProperty);
        initTreeTable();
        Task<Void> initTask = new Task<Void>() {
            private List<ReviewSet> allSets;

            @Override
            protected void scheduled() {
                progressIndicator.setVisible(true);
            }

            @Override
            protected void succeeded() {
                if (allSets == null || allSets.isEmpty()) {
                    TextInputDialog dialog = new TextInputDialog(getText("lblNewReview"));
                    dialog.setTitle(getText("titleInitialReview"));
                    dialog.setHeaderText(getText("infoNoReviewAvailable"));
                    dialog.setContentText(getText("lblReviewName")+":");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        ReviewSet rs = provider.createNewReviewSet();
                        rs.setName(result.get());
                        provider.mergeReviewSet(rs);
                        setReviewSet(rs);
                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(getText("titleErrorNoReviewSet"));
                        alert.setHeaderText(null);
                        alert.setContentText(getText("errorCannotStartWithoutReviewSet"));

                        alert.showAndWait();
                        GalRev.terminate();
                    }

                }else{
                    // TODO select active review set
                    setReviewSet(allSets.get(0));
                }
                progressIndicator.setVisible(false);
                progressIndicator.getScene().getWindow().setOnHiding(e -> executor.shutdown());
            }

            @Override
            protected Void call() throws Exception {
                allSets = provider.getAllReviewSets();
                return null;
            }
        };
        executor.submit(initTask);
        txtCurrentFile.setText("");
        currentFile.addListener(evt -> {
            if (currentFile.get() == null) {
                txtCurrentFile.setText("");
            } else {
                txtCurrentFile.setText(currentFile.getValue().getFilename());
            }
        });

        reviewSetProperty.addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                reviewSetNameProperty.setValue(newValue.getName());
                fillTree(newValue.getDirectories());
            } else {
                reviewSetNameProperty.setValue("");
                fillTree(Collections.emptyList());
            }
        }));
    }

    private void fillTree(List<RepositoryDir> directories) {


        TreeItem<DisplayPath> rootItem = createDummyTreeItem(getText("labelDirectories"));
        if (TEST_DATA) {
            rootItem.getChildren().add(createDummyTreeItem("child1"));
            rootItem.getChildren().add(createDummyTreeItem("child2"));
            rootItem.getChildren().add(createDummyTreeItem("child2"));
            rootItem.getChildren().get(0).getChildren().add(createDummyTreeItem("child1.1"));
            rootItem.getChildren().get(0).getChildren().add(createDummyTreeItem("child1.2"));
            rootItem.getChildren().get(1).getChildren().add(createDummyTreeItem("child2.1"));
            rootItem.getChildren().get(1).getChildren().add(createDummyTreeItem("child2.2"));
            rootItem.getChildren().get(1).getValue().getImageFile().setState(FileState.REVIEWED);
            rootItem.getChildren().get(0).getValue().getImageFile().setState(FileState.MARKED_FOR_DELETION);
        }else{
            directories.forEach(dir -> addChild(rootItem, dir));
        }
        ttvFiles.setRoot(rootItem);
    }

    private void addChild(TreeItem<DisplayPath> parent, RepositoryDir dir){
        TreeItem<DisplayPath> ti = createTreeItem(dir);
        parent.getChildren().add(ti);
        dir.getFiles().forEach(imageFile -> ti.getChildren().add(createTreeItem(imageFile)));
    }

    private TreeItem<DisplayPath> createTreeItem(RepositoryDir dir) {
        DisplayPath dp = new DisplayPath();
        dp.setReposDir(dir);
        dp.setPath(Paths.get(dir.getPath()));
        TreeItem<DisplayPath> ti = new TreeItem<>(dp);
        return ti;
    }

    private TreeItem<DisplayPath> createTreeItem(ImageFile file) {
        DisplayPath dp = new DisplayPath();
        dp.setImageFile(file);
        dp.setPath(Paths.get(file.getFilename()));
        TreeItem<DisplayPath> ti = new TreeItem<>(dp);
        return ti;
    }

    private void initKeyHandling(Stage stage) {

        stage.addEventHandler(KeyEvent.KEY_PRESSED, evt -> {
            System.out.println("Key pressed: " + evt.getCharacter() + "/" + evt.getText());
        });
    }

    private void initTreeTable() {
        colFile.setCellValueFactory(param -> {
            final DisplayPath displayPath = param.getValue().getValue();
            Path path = displayPath.getPath();
            String displayString="";
            if (path != null){
                displayString = path.getFileName().toString();
            }else if(displayPath.getImageFile() != null){
                displayString = displayPath.getImageFile().getFilename();
            }else if(displayPath.getReposDir() != null){
                displayString = displayPath.getReposDir().getPath();
            }
            return new SimpleStringProperty(displayString);
        });
        colAccept.setCellValueFactory(param -> {
            boolean value = false;
            if (param.getValue().getValue().getImageFile() != null){
                value = FileState.REVIEWED.equals(param.getValue().getValue().getImageFile().getState());
            }
            return new SimpleBooleanProperty(value);
        });
        colAccept.setCellFactory(param -> new TreeTableCell<DisplayPath, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                if (Boolean.TRUE.equals(item)) {
                    ImageView iv = new ImageView(UiResources.getImage(UiResources.Images.ACCEPT));
                    setGraphic(iv);
                }
            }
        });
        colDelete.setCellValueFactory(param -> {
            boolean value = false;
            if (param.getValue().getValue().getImageFile() != null){
                value = FileState.MARKED_FOR_DELETION.equals(param.getValue().getValue().getImageFile().getState());
            }
            return new SimpleBooleanProperty(value);
        });
        colDelete.setCellFactory(param -> new TreeTableCell<DisplayPath, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                if (Boolean.TRUE.equals(item)) {
                    ImageView iv = new ImageView(UiResources.getImage(UiResources.Images.DELETE));
                    setGraphic(iv);
                }
            }
        });
        ttvFiles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ttvFiles.getSelectionModel().selectedItemProperty().addListener(c -> {
            TreeItem<DisplayPath> selectedItem = ttvFiles.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                currentFile.set(selectedItem.getValue().getImageFile());
            } else {
                currentFile.set(null);
            }
        });
    }

    private TreeItem<DisplayPath> createDummyTreeItem(String filename) {
        DisplayPath root = new DisplayPath();
        root.setImageFile(new ImageFile());
        root.getImageFile().setFilename(filename);
        return new TreeItem<>(root);
    }

    private void setReviewSet(ReviewSet rs) {
        reviewSetProperty.set(rs);
    }

    private void setButtonImage(Button btn, UiResources.Images image) {
        Image img = UiResources.getImage(image);
        btn.setGraphic(new ImageView(img));
    }


    @FXML
    void accept(ActionEvent event) {

    }

    @FXML
    void add(ActionEvent event) {
        logger.debug("add invoked");

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(getText("titleSelectFolder"));
        File chooseResult = chooser.showDialog(btnAdd.getScene().getWindow());
        if (chooseResult != null) {
            addRepositoryDir(Paths.get(chooseResult.getAbsolutePath()));
        }

    }

    private void addRepositoryDir(Path directoryPath) {
        //TODO: Task
        FileCrawler crawler = new FileCrawler();
        List<CrawledEntity> resultList = crawler.crawl(directoryPath);
        resultList.forEach(ce -> {
            addCrawledEntity(null, ttvFiles.getRoot(), ce);
        });
        provider.mergeReviewSet(reviewSetProperty.get());
    }

    private void addCrawledEntity(RepositoryDir parentRD, TreeItem<DisplayPath> treeItem, CrawledEntity ce) {
        Path path = ce.getPath();
        if (Files.isDirectory(path)){
            RepositoryDir repositoryDir = reviewSetProperty.get().addDirectory(path);
            final TreeItem<DisplayPath> childDirItem = createTreeItem(repositoryDir);
            treeItem.getChildren().add(childDirItem);
            ce.getChildren().forEach(childCe -> {
                addCrawledEntity(repositoryDir, childDirItem, childCe);
            });
        }else{
            if (parentRD != null) {
                parentRD.addFile(path.getFileName().toString());
            }else{
                throw new NullPointerException("Try to add file to null repository dir");
            }
        }
    }

    @FXML
    void delete(ActionEvent event) {

    }

    @FXML
    void next(ActionEvent event) {

    }

    @FXML
    void prev(ActionEvent event) {

    }

    @FXML
    void undo(ActionEvent event) {

    }
    public static void terminate(){
        executor.shutdown();
    }

    public void selectReviewSetSelected(ActionEvent actionEvent) {
        List<String> choices = new ArrayList<>();
        Map<String, ReviewSet> allSets = provider.getAllReviewSets().stream().collect(Collectors.toMap(ReviewSet::getName, rs -> rs));

        ChoiceDialog<String> dialog = new ChoiceDialog<>(reviewSetNameProperty.get(), allSets.keySet());
        dialog.setTitle(getText("titleSelectReviewSet"));
        dialog.setHeaderText(getText("infoSelectReviewSet"));
        dialog.setContentText(getText("lableNewSet"));

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(rsName -> setReviewSet(allSets.get(rsName)));
    }

    public void addReviewSetSelected(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog(reviewSetNameProperty.get());
        dialog.setTitle(getText("titleAddReviewSet"));
        dialog.setHeaderText(getText("infoAddReviewSet"));
        dialog.setContentText(getText("labelName"));

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            ReviewSet rs = provider.createNewReviewSet();
            rs.setName(result.get());
            provider.mergeReviewSet(rs);
            reviewSetProperty.set(rs);
        }
    }

    public void commitReviewSelected(ActionEvent actionEvent) {
    }

    public void quitSelected(ActionEvent actionEvent) {
        GalRev.terminate();
    }

    public void renameReviewSetSelected(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog(reviewSetNameProperty.get());
        dialog.setTitle(getText("titleRenameReviewSet"));
        dialog.setHeaderText(getText("infoRenameReviewSet"));
        dialog.setContentText(getText("labelNewName"));

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            final ReviewSet reviewSet = reviewSetProperty.get();
            reviewSet.setName(result.get());
            provider.mergeReviewSet(reviewSet);
            reviewSetNameProperty.set(result.get());
        }
    }
}
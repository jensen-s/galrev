package org.jensen.galrev.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
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
import org.jensen.galrev.settings.GalRevSettings;
import org.jensen.galrev.ui.translate.Texts;
import org.jensen.galrev.ui.uimodel.ReviewTreeEntry;

import java.io.File;
import java.io.FileNotFoundException;
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

import static org.jensen.galrev.ui.translate.Texts.getText;

/**
 * FXML Controller for MainView
 * Created by jensen on 09.04.15.
 */
public class MainView {

    private static final String FXML_FILE_NAME = "mainview.fxml";
    private static final Logger logger = LogManager.getLogger(MainView.class);
    private static final boolean TEST_DATA = false;

    @FXML
    private SplitPane contentPane;
    @FXML
    private MenuItem miDeleteReview;
    @FXML
    private MenuItem miCommitReview;
    @FXML
    private MenuItem miRenameReviewSet;
    @FXML
    private MenuItem miMissingTexts;

    @FXML
    private ImageView ivDisplayedImage;

    @FXML
    private Text txtCurrentFile;

    @FXML
    private Label lblReviewName;

    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private TreeTableView<ReviewTreeEntry> ttvFiles;
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
    private TreeTableColumn<ReviewTreeEntry, String> colFile;
    @FXML
    private TreeTableColumn<ReviewTreeEntry, Boolean> colAccept;
    @FXML
    private TreeTableColumn<ReviewTreeEntry, Boolean> colDelete;

    private static final ExecutorService executor = Executors.newFixedThreadPool(1);

    private final ReviewProvider provider = ReviewProvider.getInstance();

    private final StringProperty reviewSetNameProperty = new SimpleStringProperty();
    private final SimpleObjectProperty<ReviewSet> reviewSetProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<ReviewTreeEntry> currentFile = new SimpleObjectProperty<>();

    public static Parent load(Stage stage) throws IOException {
        URL fxmlResource = MainView.class.getResource(FXML_FILE_NAME);
        InputStream inputStream = Texts.getBundleStream();
        ResourceBundle bundle = new PropertyResourceBundle(inputStream);
        logger.debug("Resource URL: " + fxmlResource);
        FXMLLoader loader = new FXMLLoader(fxmlResource, bundle);
        final Parent parent = loader.load();
        MainView mv = loader.getController();
        mv.initKeyHandling(stage);
        return parent;
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> progressIndicator.getScene().getWindow().setEventDispatcher((event, chain) -> {
            try {
                return chain.dispatchEvent(event);
            } catch (Exception e) {
                // TODO: Detailed error
                DialogHelper.showException(Texts.getText("messageGeneralException"), e);
                return null;
            }
        }));
        setButtonImage(btnPrev, UiResources.Images.ARROW_LEFT);
        setButtonImage(btnDelete, UiResources.Images.DELETE);
        setButtonImage(btnUndo, UiResources.Images.UNDO);
        setButtonImage(btnAdd, UiResources.Images.FOLDER_ADD);
        setButtonImage(btnAccept, UiResources.Images.ACCEPT);
        setButtonImage(btnNext, UiResources.Images.ARROW_RIGHT);
        ivDisplayedImage.setImage(UiResources.getImage(UiResources.Images.IMAGE_PLACEHOLDER));
        lblReviewName.textProperty().bind(reviewSetNameProperty);

        txtCurrentFile.setText("");
        currentFile.addListener(evt -> {
            if (currentFile.get() == null) {
                txtCurrentFile.setText("");
                ivDisplayedImage.imageProperty().setValue(null);
            } else {
                ReviewTreeEntry treeEntry = currentFile.getValue();
                txtCurrentFile.setText(treeEntry.getImageFile().getFilename());
                if (treeEntry.getPath() != null) {
                    try {
                        final Image image = retrieveImage(treeEntry);
                        ivDisplayedImage.setImage(image);
                        layoutImageView();
                        ivDisplayedImage.setCache(true);
                        ivDisplayedImage.setVisible(true);
                    } catch (FileNotFoundException e) {
                        logger.error("File not existent: " + treeEntry.getPath());
                        // TODO: Show error
                    }
                }
                if (treeEntry.getImageFile() != null) {
                    switch (treeEntry.getImageFile().getState()) {
                        case NEW:
                            btnAccept.setDisable(false);
                            btnDelete.setDisable(false);
                            btnUndo.setDisable(true);
                            break;
                        case REVIEWED:
                        case MARKED_FOR_DELETION:
                            btnAccept.setDisable(true);
                            btnDelete.setDisable(true);
                            btnUndo.setDisable(false);
                            break;
                        case DELETED:
                        case LOST:
                            btnAccept.setDisable(false);
                            btnDelete.setDisable(false);
                            btnUndo.setDisable(false);
                            break;
                    }
                } else {
                    btnAccept.setDisable(false);
                    btnDelete.setDisable(false);
                    btnUndo.setDisable(false);
                }
            }
        });

        reviewSetProperty.addListener(((observable, oldValue, newValue) -> {
            handleReviewSetSelected(newValue);
        }));
        ((Region) ivDisplayedImage.getParent()).heightProperty().addListener((a, b, c) -> layoutImageView());
        ((Region) ivDisplayedImage.getParent()).widthProperty().addListener((a, b, c) -> layoutImageView());
        initTreeTable();
        miMissingTexts.setVisible(GalRevSettings.isDeveloperMode());
        loadAsyncData();
    }

    private void handleReviewSetSelected(ReviewSet newValue) {
        final boolean reviewMissing = newValue == null;
        miCommitReview.setDisable(reviewMissing);
        miDeleteReview.setDisable(reviewMissing);
        miRenameReviewSet.setDisable(reviewMissing);
        contentPane.setDisable(reviewMissing);
        if (!reviewMissing) {
            reviewSetNameProperty.setValue(newValue.getName());
            fillTree(newValue.getDirectories());
        } else {
            reviewSetNameProperty.setValue("");
            fillTree(Collections.emptyList());
        }
    }

    private void layoutImageView() {
        Image image = ivDisplayedImage.getImage();
        if (image != null) {
            final double fitWidth = image.getWidth();
            logger.debug("fit width: " + fitWidth);
            double layoutHeight = ((Region) ivDisplayedImage.getParent()).getHeight();
            double layoutWidth = ((Region) ivDisplayedImage.getParent()).getWidth();
            final double fitHeight = image.getHeight();
            ivDisplayedImage.setFitWidth(Math.min(fitWidth, layoutWidth));
            ivDisplayedImage.setFitHeight(Math.min(fitHeight, layoutHeight));
        }
    }

    private Image retrieveImage(ReviewTreeEntry ReviewTreeEntry) throws FileNotFoundException {

        return ImageService.getInstance().getImage(ReviewTreeEntry.getPath());
    }

    private void loadAsyncData() {
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
    }

    private void fillTree(List<RepositoryDir> directories) {


        TreeItem<ReviewTreeEntry> rootItem = createDummyTreeItem(getText("labelDirectories"));
        List<Path> missingPaths = UiHelper.fillTreeItem(rootItem, directories);
        if (!missingPaths.isEmpty()) {
            throw new RuntimeException("Implement handling of missing paths!");
        }
        ttvFiles.setRoot(rootItem);
    }

    private void addChild(TreeItem<ReviewTreeEntry> parent, RepositoryDir dir) {
        TreeItem<ReviewTreeEntry> ti = createTreeItem(dir);
        parent.getChildren().add(ti);
        dir.getFiles().forEach(imageFile -> ti.getChildren().add(createTreeItem(parent, imageFile)));
    }

    private TreeItem<ReviewTreeEntry> createTreeItem(RepositoryDir dir) {
        ReviewTreeEntry dp = new ReviewTreeEntry(dir, Paths.get(dir.getPath()));
        return new TreeItem<>(dp);
    }

    private TreeItem<ReviewTreeEntry> createTreeItem(TreeItem<ReviewTreeEntry> parent, ImageFile file) {
        ReviewTreeEntry dp = new ReviewTreeEntry(parent.getValue().getPath(), file);
        return new TreeItem<>(dp);
    }

    private void initKeyHandling(Stage stage) {

        stage.addEventHandler(KeyEvent.KEY_PRESSED, evt -> System.out.println("Key pressed: " + evt.getCharacter() + "/" + evt.getText()));
    }

    private void initTreeTable() {
        colFile.setCellValueFactory(new TreeItemPropertyValueFactory<>("fileName"));

        colFile.setCellFactory(tv -> {
            final TreeTableCell<ReviewTreeEntry, String> cell = new TreeTableCell<ReviewTreeEntry, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        ReviewTreeEntry treeEntry = getTreeTableRow().getItem();
                        if (treeEntry != null) {
                            Path path = treeEntry.getPath();
                            String displayString;
                            if (path != null) {
                                setTooltip(new Tooltip(path.toString()));
                            } else {
                                setTooltip(null);
                            }
                            displayString = treeEntry.getFileName();
                            setText(displayString);
                        }
                    } else {
                        setText(null);
                    }
                }
            };

            return cell;
        });
        colFile.setCellValueFactory(param -> {
            final ReviewTreeEntry ReviewTreeEntry = param.getValue().getValue();

            String displayString="";

            return new SimpleStringProperty(displayString);
        });
        colAccept.setCellValueFactory(new TreeItemPropertyValueFactory<>("accepted"));
        colAccept.setCellFactory(param -> new TreeTableCell<ReviewTreeEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                if (!empty && Boolean.TRUE.equals(item)) {
                    ImageView iv = new ImageView(UiResources.getImage(UiResources.Images.ACCEPT));
                    setGraphic(iv);
                } else {
                    setGraphic(null);
                }
            }
        });
        colDelete.setCellValueFactory(new TreeItemPropertyValueFactory<>("toDelete"));
        colDelete.setCellFactory(param -> new TreeTableCell<ReviewTreeEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                if (!empty && Boolean.TRUE.equals(item)) {
                    ImageView iv = new ImageView(UiResources.getImage(UiResources.Images.DELETE));
                    setGraphic(iv);
                } else {
                    setGraphic(null);
                }
            }
        });
        ttvFiles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ttvFiles.getSelectionModel().selectedItemProperty().addListener(c -> {
            TreeItem<ReviewTreeEntry> selectedItem = ttvFiles.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() != null) {
                currentFile.set(selectedItem.getValue());
            } else {
                currentFile.set(null);
            }
        });
    }

    private TreeItem<ReviewTreeEntry> createDummyTreeItem(String filename) {
        ImageFile imageFile = new ImageFile();
        imageFile.setFilename(filename);
        ReviewTreeEntry root = new ReviewTreeEntry(null, imageFile);
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
        ReviewTreeEntry treeEntry = currentFile.get();
        if (treeEntry != null) {
            treeEntry.setFileState(FileState.REVIEWED);
        }
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
        resultList.forEach(ce -> addCrawledEntity(null, ttvFiles.getRoot(), ce));
        provider.mergeReviewSet(reviewSetProperty.get());
    }

    private void addCrawledEntity(RepositoryDir parentRD, TreeItem<ReviewTreeEntry> treeItem, CrawledEntity ce) {
        Path path = ce.getPath();
        if (Files.isDirectory(path)){
            RepositoryDir repositoryDir = reviewSetProperty.get().addDirectory(path);
            final TreeItem<ReviewTreeEntry> childDirItem = createTreeItem(repositoryDir);
            treeItem.getChildren().add(childDirItem);
            ce.getChildren().forEach(childCe -> addCrawledEntity(repositoryDir, childDirItem, childCe));
        }else{
            if (parentRD != null) {
                ImageFile imageFile = parentRD.addFile(path.getFileName().toString());
                final TreeItem<ReviewTreeEntry> childDirItem = createTreeItem(treeItem, imageFile);
                treeItem.getChildren().add(childDirItem);
            }else{
                throw new NullPointerException("Try to add file to null repository dir");
            }
        }
    }

    @FXML
    void delete(ActionEvent event) {
        ReviewTreeEntry treeEntry = currentFile.get();
        if (treeEntry != null) {
            treeEntry.setFileState(FileState.MARKED_FOR_DELETION);
        }
    }

    @FXML
    void next(ActionEvent event) {

    }

    @FXML
    void prev(ActionEvent event) {

    }

    @FXML
    void undo(ActionEvent event) {
        ReviewTreeEntry treeEntry = currentFile.get();
        if (treeEntry != null) {
            treeEntry.setFileState(FileState.NEW);
        }
    }
    public static void terminate(){
        executor.shutdown();
    }

    public void selectReviewSetSelected(ActionEvent actionEvent) {
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

    public void deleteReviewSetSelected(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(getText("titleDeleteReviewSet"));
        alert.setContentText(getText("questionDeleteReviewSet"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            provider.deleteReviewSet(reviewSetProperty.get());
            reviewSetProperty.set(null);
        }


    }

    @FXML
    private void missingTextsSelected(ActionEvent actionEvent) {
        MissingTexts.open();
    }

}
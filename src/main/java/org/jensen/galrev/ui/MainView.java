package org.jensen.galrev.ui;

import static org.jensen.galrev.ui.translate.Texts.getText;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jensen.galrev.app.GalRev;
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
    private ReviewSet reviewSet;
    private SimpleObjectProperty<ImageFile> currentFile = new SimpleObjectProperty<>();

    public static Parent load() throws IOException {
        URL fxmlResource = MainView.class.getResource(FXML_FILE_NAME);
        InputStream inputStream = Texts.getBundleStream();
        ResourceBundle bundle = new PropertyResourceBundle(inputStream);
        logger.debug("Resource URL: " + fxmlResource);
        return FXMLLoader.load(fxmlResource, bundle);
    }

    @FXML
    void initialize() {
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
            if (currentFile.get() == null){
                txtCurrentFile.setText("");
            }else{
                txtCurrentFile.setText(currentFile.getValue().getFilename());
            }
        });

        //TODO: Test data
        TreeItem<DisplayPath> rootItem = createTreeItem("root");

        rootItem.getChildren().add(createTreeItem("child1"));
        rootItem.getChildren().add(createTreeItem("child2"));
        rootItem.getChildren().add(createTreeItem("child2"));
        rootItem.getChildren().get(0).getChildren().add(createTreeItem("child1.1"));
        rootItem.getChildren().get(0).getChildren().add(createTreeItem("child1.2"));
        rootItem.getChildren().get(1).getChildren().add(createTreeItem("child2.1"));
        rootItem.getChildren().get(1).getChildren().add(createTreeItem("child2.2"));
        rootItem.getChildren().get(1).getValue().getImageFile().setState(FileState.REVIEWED);
        rootItem.getChildren().get(0).getValue().getImageFile().setState(FileState.MARKED_FOR_DELETION);
        ttvFiles.setRoot(rootItem);
    }

    private void initTreeTable() {
        colFile.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getImageFile().getFilename()));
        colAccept.setCellValueFactory(param -> new SimpleBooleanProperty(FileState.REVIEWED.equals(param.getValue().getValue().getImageFile().getState())));
        colAccept.setCellFactory(param -> {
            return new TreeTableCell<DisplayPath, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    if (Boolean.TRUE.equals(item)) {
                        ImageView iv = new ImageView(UiResources.getImage(UiResources.Images.ACCEPT));
                        setGraphic(iv);
                    }
                }
            };
        });
        colDelete.setCellValueFactory(param -> new SimpleBooleanProperty(FileState.MARKED_FOR_DELETION.equals(param.getValue().getValue().getImageFile().getState())));
        colDelete.setCellFactory(param -> {
            return new TreeTableCell<DisplayPath, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    if (Boolean.TRUE.equals(item)) {
                        ImageView iv = new ImageView(UiResources.getImage(UiResources.Images.DELETE));
                        setGraphic(iv);
                    }
                }
            };
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

    private TreeItem<DisplayPath> createTreeItem(String filename) {
        DisplayPath root = new DisplayPath();
        root.setImageFile(new ImageFile());
        root.getImageFile().setFilename(filename);
        return new TreeItem<>(root);
    }

    private void setReviewSet(ReviewSet rs) {
        this.reviewSet = rs;
        reviewSetNameProperty.set(rs.getName());
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
            RepositoryDir rd = reviewSet.addDirectory(Paths.get(chooseResult.getAbsolutePath()));
            addRepositoryDir(rd);
        }

    }

    private void addRepositoryDir(RepositoryDir rd) {
        FileCrawler crawler = new FileCrawler();
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

        ChoiceDialog<String> dialog = new ChoiceDialog<>(reviewSet.getName(), allSets.keySet());
        dialog.setTitle(getText("titleSelectReviewSet"));
        dialog.setHeaderText(getText("infoSelectReviewSet"));
        dialog.setContentText(getText("lableNewSet"));

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(rsName -> setReviewSet(allSets.get(rsName)));
    }

    public void addReviewSetSelected(ActionEvent actionEvent) {
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
            reviewSet.setName(result.get());
            provider.mergeReviewSet(reviewSet);
            reviewSetNameProperty.set(result.get());
        }
    }
}
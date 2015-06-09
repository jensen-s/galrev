package org.jensen.galrev.ui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jensen.galrev.model.ReviewProvider;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FXML Controller for MainView
 * Created by jensen on 09.04.15.
 */
public class MainView {

    private static final String FXML_FILE_NAME = "mainview.fxml";
    private static final Logger logger = LogManager.getLogger(MainView.class);

    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private TreeTableView ttvFiles;
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
    private TreeTableColumn colFile;
    @FXML
    private TreeTableColumn colAccept;
    @FXML
    private TreeTableColumn colDelete;
    private ExecutorService executor;

    public static Parent load() throws IOException {
        URL fxmlResource = MainView.class.getResource(FXML_FILE_NAME);
        logger.debug("Resource URL: " + fxmlResource);
        return FXMLLoader.load(fxmlResource);
    }

    @FXML
    void initialize() {
        setButtonImage(btnPrev, UiResources.Images.ARROW_LEFT);
        setButtonImage(btnDelete, UiResources.Images.DELETE);
        setButtonImage(btnUndo, UiResources.Images.UNDO);
        setButtonImage(btnAdd, UiResources.Images.FOLDER_ADD);
        setButtonImage(btnAccept, UiResources.Images.ACCEPT);
        setButtonImage(btnNext, UiResources.Images.ARROW_RIGHT);
        Task<Void> initTask = new Task<Void>() {
            @Override
            protected void scheduled() {
                progressIndicator.setVisible(true);
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
                progressIndicator.getScene().getWindow().setOnHiding( e -> executor.shutdown());
            }

            @Override
            protected Void call() throws Exception {
                ReviewProvider.getInstance().getAllReviewSets();
                return null;
            }
        };
        this.executor = Executors.newFixedThreadPool(1);
        executor.submit(initTask);
    }

    private void setButtonImage(Button btn, UiResources.Images image) {
        Image img     = UiResources.getImage(image);
        btn.setGraphic(new ImageView(img));
    }


    @FXML
    void accept(ActionEvent event) {

    }

    @FXML
    void add(ActionEvent event) {
        logger.debug("add invoked");

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


}
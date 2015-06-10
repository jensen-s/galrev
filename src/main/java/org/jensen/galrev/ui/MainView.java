package org.jensen.galrev.ui;

import static org.jensen.galrev.ui.translate.Texts.getText;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jensen.galrev.app.GalRev;
import org.jensen.galrev.model.ReviewProvider;
import org.jensen.galrev.model.entities.ReviewSet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
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
    private Label lblReviewName;

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

    private static ExecutorService executor = Executors.newFixedThreadPool(1);

    private ReviewProvider provider = ReviewProvider.getInstance();

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

                    // Traditional way to get the response value.
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

    private void setReviewSet(ReviewSet rs) {
        lblReviewName.setText(rs.getName());

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

}
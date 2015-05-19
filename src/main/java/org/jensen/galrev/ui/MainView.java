package org.jensen.galrev.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * FXML Controller for MainView
 * Created by jensen on 09.04.15.
 */
public class MainView {

    private static final String FXML_FILE_NAME = "mainview.fxml";
    private static final Logger logger = LogManager.getLogger(MainView.class);

    @FXML
    private TreeTableView ttvFiles;
    @FXML
    private Button btnPrev;
    @FXML
    private Button btnDelete;
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
        setButtonImage(btnAccept, UiResources.Images.ACCEPT);
        setButtonImage(btnNext, UiResources.Images.ARROW_RIGHT);
    }

    private void setButtonImage(Button btn, UiResources.Images image) {
        Image img     = UiResources.getImage(image);
        btn.setGraphic(new ImageView(img));
    }
}
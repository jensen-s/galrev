package org.jensen.galrev.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Created by jensen on 09.04.15.
 */
public class MainView {

    private static final String FXML_FILE_NAME = "mainview.fxml";
    private static final Logger logger = LogManager.getLogger(MainView.class);

    public static Parent load() throws IOException {
        URL fxmlResource = MainView.class.getResource(FXML_FILE_NAME);
        logger.debug("Resource URL: " + fxmlResource);
        return FXMLLoader.load(fxmlResource);
    }
}
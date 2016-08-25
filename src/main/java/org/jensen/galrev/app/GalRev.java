package org.jensen.galrev.app;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jensen.galrev.model.JpaAccess;
import org.jensen.galrev.settings.GalRevSettings;
import org.jensen.galrev.ui.MainView;
import org.jensen.galrev.ui.translate.Texts;

import java.util.Optional;

import static org.jensen.galrev.ui.translate.Texts.getText;

public class GalRev extends Application{

    private static final Logger logger = LogManager.getLogger(GalRev.class);


    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Application starting: " + AppInfo.getFullApplicationName());
        Parent root = MainView.load(primaryStage);
        primaryStage.setTitle(AppInfo.getFullApplicationName());
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.setOnCloseRequest(e -> terminate());
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        logger.debug("Start application initialization");
        JpaAccess.setPersistenceUnit(GalRevSettings.getPersistenceUnit());
        logger.debug("Initialization done");
    }

    public static void main(String[] args) {
        // TODO: Lock file and show error if not possible
        Application.launch(GalRev.class);
    }

    public static void terminate() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(getText("titleConfirmQuit"));
        alert.setHeaderText(getText("questionConfirmQuit"));
        alert.setContentText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // TODO: Delete lock file
            MainView.terminate();
            Texts.printMissingTexts();
            System.exit(0);
        }
    }
}

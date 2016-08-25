package org.jensen.galrev.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jensen.galrev.ui.translate.Texts;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Created by jensen on 25.08.16.
 */
public class MissingTexts implements Initializable {
    private static final String FXML_FILE = "missingTexts.fxml";

    @FXML
    private TextArea tfMissingTexts;

    public static void open() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MissingTexts.class.getResource(FXML_FILE));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Missing texts");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Internal error: Missing resource " + FXML_FILE);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HashMap<String, HashSet<String>> missing = Texts.getMissingTexts();
        StringBuilder b = new StringBuilder();
        missing.keySet().forEach(localeStr -> {
            b.append("Locale: ").append(localeStr).append("\n============\n");
            missing.get(localeStr).forEach(key -> b.append(key).append(" = \n"));
            b.append("\n");
        });
        tfMissingTexts.setText(b.toString());
    }

    @FXML
    private void closeSelected(ActionEvent actionEvent) {
        ((Stage) tfMissingTexts.getScene().getWindow()).close();
    }
}

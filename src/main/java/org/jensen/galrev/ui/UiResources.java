package org.jensen.galrev.ui;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by jensen on 19.05.15.
 */
public class UiResources {

    private static final Logger logger = LogManager.getLogger();

    public static Image getImage(Images img){
        final String fileName = "/org/jensen/galrev/ui/images/" + img.getFilename();
        logger.debug("Serve resource " + fileName);
        return new Image(UiResources.class.getResourceAsStream(fileName));
    }

    public enum Images {
        IMAGE_PLACEHOLDER("image-placeholder"),
        ARROW_LEFT("arrow-left"),
        ARROW_RIGHT("arrow-right"),
        DELETE("delete"),
        DELETED("deleted"),
        FOLDER_ADD("folder-add"),
        ACCEPT("accept"),
        UNDO("undo");
        private final String filename;

        Images(String filename){
            if (!filename.contains(".")){
                filename += ".png";
            }
            this.filename=filename;
        }

        public String getFilename() {
            return filename;
        }
    }
}

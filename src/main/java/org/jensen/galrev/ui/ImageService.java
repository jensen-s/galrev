package org.jensen.galrev.ui;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * Created by jensen on 17.12.15.
 */
public class ImageService {
    private static ImageService instance = new ImageService();
    private Logger logger = LogManager.getLogger(ImageService.class);

    public static ImageService getInstance() {
        return instance;
    }

    private ImageService() {
    }


    public Image getImage(Path imagePath) throws FileNotFoundException {
        logger.debug("Load image from path " + imagePath);
        final Image image = new Image(imagePath.toUri().toString());

        logger.debug("Loaded image " + image);
        return image;
    }
}

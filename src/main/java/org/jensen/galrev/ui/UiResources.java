package org.jensen.galrev.ui;

import javafx.scene.image.Image;

/**
 * Created by jensen on 19.05.15.
 */
public class UiResources {
    public static Image getImage(Images img){
        final String fileName = "/org/jensen/galrev/ui/images/" + img.getFilename();
        System.out.println(fileName);
        return new Image(UiResources.class.getResourceAsStream(fileName));
    }

    public enum Images {
        ARROW_LEFT("arrow-left"),
        ARROW_RIGHT("arrow-right"),
        DELETE("delete"),
        DELETED("deleted"),
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

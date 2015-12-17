package org.jensen.galrev.ui;

import org.jensen.galrev.model.entities.ImageFile;

import java.nio.file.Path;

/**
 * Created by jensen on 17.12.15.
 */
public class DisplayImage extends DisplayPath {
    private ImageFile imageFile;

    public DisplayImage(Path parentPath, ImageFile imageFile) {
        this.imageFile = imageFile;
        if (parentPath != null) {
            setPath(parentPath.resolve(imageFile.getFilename()));
        }
    }

    public ImageFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    @Override
    public String toString() {
        return "DisplayImage{" +
                "imageFile=" + imageFile +
                '}';
    }
}

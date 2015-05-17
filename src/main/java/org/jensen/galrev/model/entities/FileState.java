package org.jensen.galrev.model.entities;

/**
 * review state of an ImageFile
 * Created by jensen on 07.05.15.
 */
public enum FileState {
    /**
     * freshly added to review, not handled
     */
    NEW,
    /**
     * reviewed and considered ok
     */
    REVIEWED,
    /**
     * reviewed and considered as to be deleted
     */
    MARKED_FOR_DELETION,
    /**
     * reviewed and deleted (physically not existing)
     */
    DELETED,
    /**
     * physically not existing, but not deleted by GalleryReview
     */
    LOST
}

package ug.edu.pl.server.domain.common.storage.exception;

public class DeletingImageException extends RuntimeException {
    public DeletingImageException() {
        super("Deleting image failed.");
    }
}

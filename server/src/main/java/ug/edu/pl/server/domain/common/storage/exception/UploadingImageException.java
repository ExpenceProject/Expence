package ug.edu.pl.server.domain.common.storage.exception;

public class UploadingImageException extends RuntimeException {
    public UploadingImageException() {
        super("Uploading image failed");
    }
}

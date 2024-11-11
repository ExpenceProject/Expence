package ug.edu.pl.server.infrastructure.storage.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ug.edu.pl.server.domain.common.storage.exception.DeletingImageException;
import ug.edu.pl.server.domain.common.storage.exception.UploadingImageException;
import ug.edu.pl.server.infrastructure.base.BaseExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class StorageExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(UploadingImageException.class)
    ResponseEntity<ErrorDto> handleUploadingImageException(UploadingImageException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorDto.builder()
                        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                        .status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .message(ex.getMessage())
                        .path(getPath(request))
                        .timestamp(Instant.now())
                        .build());
    }

    @ExceptionHandler(DeletingImageException.class)
    ResponseEntity<ErrorDto> handleDeletingImageException(DeletingImageException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorDto.builder()
                        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                        .status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .message(ex.getMessage())
                        .path(getPath(request))
                        .timestamp(Instant.now())
                        .build());
    }
}

package ug.edu.pl.server.infrastructure.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ug.edu.pl.server.domain.common.exception.*;
import ug.edu.pl.server.infrastructure.base.BaseExceptionHandler;

import java.time.Instant;

@ControllerAdvice
class ExceptionHandler extends BaseExceptionHandler {
  @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
  ResponseEntity<ErrorDto> handleNotFoundException(
          NotFoundException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                    ErrorDto.builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .status(HttpStatus.NOT_FOUND)
                            .message(ex.getMessage())
                            .path(getPath(request))
                            .timestamp(Instant.now())
                            .build());
  }
  @org.springframework.web.bind.annotation.ExceptionHandler(SavingException.class)
  ResponseEntity<ErrorDto> handleSavingException(SavingException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(
                    ErrorDto.builder()
                            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                            .status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .message(ex.getMessage())
                            .path(getPath(request))
                            .timestamp(Instant.now())
                            .build());
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(ForbiddenException.class)
  ResponseEntity<ErrorDto> handleForbiddenException(ForbiddenException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(
                    ErrorDto.builder()
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .status(HttpStatus.FORBIDDEN)
                            .message(ex.getMessage())
                            .path(getPath(request))
                            .timestamp(Instant.now())
                            .build());
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(DuplicateException.class)
  ResponseEntity<ErrorDto> handleDuplicateException(DuplicateException ex, WebRequest request) {
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorDto.builder()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .status(HttpStatus.CONFLICT)
                    .message(ex.getMessage())
                    .path(getPath(request))
                    .timestamp(Instant.now())
                    .build());
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(DeleteException.class)
  ResponseEntity<ErrorDto> handleDeleteException(DeleteException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                    ErrorDto.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(ex.getMessage())
                            .path(getPath(request))
                            .timestamp(Instant.now())
                            .build());
  }
}

package ug.edu.pl.server.infrastructure.group.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ug.edu.pl.server.domain.group.exception.*;
import ug.edu.pl.server.infrastructure.base.BaseExceptionHandler;

import java.time.Instant;

@ControllerAdvice
class GroupExceptionHandler extends BaseExceptionHandler {
  @ExceptionHandler(GroupNotFoundException.class)
  ResponseEntity<ErrorDto> handleGroupNotFoundException(
      GroupNotFoundException ex, WebRequest request) {
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

  @ExceptionHandler(GroupRoleNotFoundException.class)
  ResponseEntity<ErrorDto> handleGroupRoleNotFoundException(
      GroupRoleNotFoundException ex, WebRequest request) {
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

  @ExceptionHandler(SavingGroupException.class)
  ResponseEntity<ErrorDto> handleSavingGroupException(SavingGroupException ex, WebRequest request) {
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

  @ExceptionHandler(NotFoundException.class)
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
  @ExceptionHandler(SavingException.class)
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
}

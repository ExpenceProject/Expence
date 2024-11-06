package ug.edu.pl.server.infrastructure.user.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ug.edu.pl.server.domain.user.exception.RoleNotFoundException;
import ug.edu.pl.server.domain.user.exception.SavingUserException;
import ug.edu.pl.server.domain.user.exception.UserAlreadyExistsException;
import ug.edu.pl.server.domain.user.exception.UserNotFoundException;
import ug.edu.pl.server.infrastructure.base.BaseExceptionHandler;

import java.time.Instant;

@ControllerAdvice
class UserExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ErrorDto> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .status(HttpStatus.NOT_FOUND)
                        .message(ex.getMessage())
                        .path(getPath(request))
                        .timestamp(Instant.now())
                        .build());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    ResponseEntity<ErrorDto> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
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

    @ExceptionHandler(RoleNotFoundException.class)
    ResponseEntity<ErrorDto> handleRoleNotFoundException(RoleNotFoundException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .status(HttpStatus.NOT_FOUND)
                        .message(ex.getMessage())
                        .path(getPath(request))
                        .timestamp(Instant.now())
                        .build());
    }

    @ExceptionHandler(SavingUserException.class)
    ResponseEntity<ErrorDto> handleSavingUserException(SavingUserException ex, WebRequest request) {
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

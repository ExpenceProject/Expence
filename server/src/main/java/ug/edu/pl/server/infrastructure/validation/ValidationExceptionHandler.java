package ug.edu.pl.server.infrastructure.validation;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ug.edu.pl.server.infrastructure.base.BaseExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
class ValidationExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ValidationErrorDto> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        var errors = getErrorsFromException(ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorDto(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, errors, getPath(request), Instant.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationErrorDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        var errors = getErrorsFromException(ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorDto(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, errors, getPath(request), Instant.now()));
    }

    private Map<String, String> getErrorsFromException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        Objects.requireNonNull(ex.getBindingResult()).getAllErrors().forEach(error -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    private Map<String, String> getErrorsFromException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = null;
            for (var node : violation.getPropertyPath()) {
                fieldName = node.getName();
            }
            var errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}

record ValidationErrorDto(int statusCode, HttpStatus status, Map<String, String> messages, String path,
                          Instant timestamp) {
}

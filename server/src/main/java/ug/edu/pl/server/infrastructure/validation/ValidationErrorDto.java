package ug.edu.pl.server.infrastructure.validation;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

record ValidationErrorDto(int statusCode, HttpStatus status, Map<String, String> messages, String path,
                          Instant timestamp) {
}

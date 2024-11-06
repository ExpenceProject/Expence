package ug.edu.pl.server.infrastructure.base;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

public abstract class BaseExceptionHandler {

    protected String getPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }

    @Builder
    protected record ErrorDto(int statusCode, HttpStatus status, String message, String path, Instant timestamp) {
    }
}

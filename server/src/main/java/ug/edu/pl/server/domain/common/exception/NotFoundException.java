package ug.edu.pl.server.domain.common.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String object, Long id) {
        super("%s with id '%s' not found".formatted(object, id));
    }

    public NotFoundException(String object) {
        super("%s not found".formatted(object));
    }
}

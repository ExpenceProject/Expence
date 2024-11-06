package ug.edu.pl.server.domain.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with id '%s' not found".formatted(id));
    }
}

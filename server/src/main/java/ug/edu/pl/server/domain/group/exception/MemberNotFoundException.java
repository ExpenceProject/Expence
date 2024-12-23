package ug.edu.pl.server.domain.group.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long id) {
        super("Member with id '%s' not found".formatted(id));
    }
}

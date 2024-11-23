package ug.edu.pl.server.domain.group.exception;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(Long id) {
        super("Group with id '%s' not found".formatted(id));
    }
}

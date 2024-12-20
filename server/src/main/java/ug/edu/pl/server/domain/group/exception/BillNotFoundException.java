package ug.edu.pl.server.domain.group.exception;

public class BillNotFoundException extends RuntimeException{
    public BillNotFoundException(Long id) {
        super("Bill with id '%s' not found".formatted(id));
    }
}

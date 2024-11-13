package ug.edu.pl.server.domain.common.event;

public record TestDomainEvent(int id) implements DomainEvent {
    public TestDomainEvent() {
        this(1);
    }
}

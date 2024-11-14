package ug.edu.pl.server.domain.common.event;

public class TestEventConfiguration {

    public DomainEventPublisher domainEventPublisher() {
        return new EventConfiguration().domainEventPublisher(new InMemoryEventRepository());
    }

    DomainEventPublisher domainEventPublisher(EventRepository eventRepository) {
        return new EventConfiguration().domainEventPublisher(eventRepository);
    }

    EventFacade eventFacade(EventRepository eventRepository) {
        return new EventConfiguration().eventFacade(eventRepository, new InMemoryEventPublisher());
    }
}

package ug.edu.pl.server.domain.common.event;

import org.springframework.transaction.annotation.Transactional;

public class DomainEventPublisher {

    private final EventRepository eventRepository;

    DomainEventPublisher(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public void publish(DomainEvent event) {
        eventRepository.save(new Event(event));
    }
}

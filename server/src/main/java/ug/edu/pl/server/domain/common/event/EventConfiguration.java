package ug.edu.pl.server.domain.common.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EventConfiguration {

    @Bean
    DomainEventPublisher domainEventPublisher(EventRepository eventRepository) {
        return new DomainEventPublisher(eventRepository);
    }

    @Bean
    EventFacade eventFacade(EventRepository eventRepository, ApplicationEventPublisher publisher) {
        return new EventFacade(eventRepository, publisher);
    }
}

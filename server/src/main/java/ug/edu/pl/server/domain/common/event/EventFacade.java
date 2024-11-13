package ug.edu.pl.server.domain.common.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import ug.edu.pl.server.Log;

@Slf4j
@Log
public class EventFacade {

    private final EventRepository eventRepository;
    private final ApplicationEventPublisher publisher;

    EventFacade(EventRepository eventRepository, ApplicationEventPublisher publisher) {
        this.eventRepository = eventRepository;
        this.publisher = publisher;
    }

    @Transactional
    public void publishAllPendingEvents() {
        var events = eventRepository.findAllByEventStatus(EventStatus.PENDING);

        for (var event : events) {
            try {
                var domainEvent = EventMapper.deserializeEvent(event);
                publisher.publishEvent(domainEvent);
                event.setEventStatus(EventStatus.PUBLISHED);
            } catch (Exception e) {
                log.error("Error while publishing event", e);
            }
        }
    }

    @Transactional
    public void cleanUpPublishedEvents() {
        try {
            eventRepository.deleteAllByEventStatus(EventStatus.PUBLISHED);
        } catch (Exception e) {
            log.error("Error while cleaning up published events", e);
        }
    }
}

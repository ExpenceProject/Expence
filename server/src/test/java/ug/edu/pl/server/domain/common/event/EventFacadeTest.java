package ug.edu.pl.server.domain.common.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventFacadeTest {

    EventRepository eventRepository = new InMemoryEventRepository();
    EventFacade eventFacade = new EventFacade(eventRepository, new InMemoryEventPublisher());

    @Test
    void shouldPublishPendingEventsAndMarksAsPublished() {
        // given
        eventRepository.save(new Event(new TestDomainEvent()));

        // when
        eventFacade.publishAllPendingEvents();

        // then
        var events = eventRepository.findAllByEventStatus(EventStatus.PUBLISHED);
        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    void shouldCleanUpPublishedEvents() {
        // given
        eventRepository.save(new Event(new TestDomainEvent()));
        eventFacade.publishAllPendingEvents();

        // when
        eventFacade.cleanUpPublishedEvents();

        // then
        var events = eventRepository.findAllByEventStatus(EventStatus.PUBLISHED);
        assertThat(events.size()).isEqualTo(0);
    }
}

package ug.edu.pl.server.domain.common.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventPublisherTest {

    EventRepository eventRepository = new InMemoryEventRepository();
    DomainEventPublisher publisher = new TestEventConfiguration().domainEventPublisher(eventRepository);

    @Test
    void shouldSaveEventWithPendingStatus() {
        // given
        var event = new TestDomainEvent();

        // when
        publisher.publish(event);

        // then
        var events = eventRepository.findAllByEventStatus(EventStatus.PENDING);
        assertThat(events.size()).isEqualTo(1);

        var savedEvent = events.iterator().next();
        assertThat(savedEvent.getEventStatus()).isEqualTo(EventStatus.PENDING);
        assertThat(savedEvent.getSerializedEvent()).isNotNull();
        assertThat(savedEvent.getEventType()).isEqualTo(event.getClass().getName());
    }
}


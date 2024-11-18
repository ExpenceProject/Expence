package ug.edu.pl.server.domain.common.event;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import ug.edu.pl.server.domain.common.persistance.BaseEntity;

@NoArgsConstructor
@Entity
@Table(name = "events")
class Event extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String serializedEvent;

    @Column(nullable = false, updatable = false)
    private String eventType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    Event(DomainEvent domainEvent) {
        this.eventType = domainEvent.getClass().getName();
        this.serializedEvent = EventMapper.serializeEvent(domainEvent);
        this.eventStatus = EventStatus.PENDING;
    }

    String getSerializedEvent() {
        return serializedEvent;
    }

    String getEventType() {
        return eventType;
    }

    EventStatus getEventStatus() {
        return eventStatus;
    }

    void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
}

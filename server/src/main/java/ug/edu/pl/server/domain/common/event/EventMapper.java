package ug.edu.pl.server.domain.common.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class EventMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    static String serializeEvent(DomainEvent event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }

    static DomainEvent deserializeEvent(Event event) {
        try {
            Class<?> eventClass = Class.forName(event.getEventType());

            if (!DomainEvent.class.isAssignableFrom(eventClass)) {
                throw new IllegalArgumentException("Event type " + event.getEventType() + " is not a subclass of " + DomainEvent.class.getName());
            }

            return (DomainEvent) mapper.readValue(event.getSerializedEvent(), eventClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize event", e);
        }
    }
}

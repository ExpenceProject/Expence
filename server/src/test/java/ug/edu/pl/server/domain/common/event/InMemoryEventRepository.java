package ug.edu.pl.server.domain.common.event;

import ug.edu.pl.server.base.InMemoryRepository;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class InMemoryEventRepository implements EventRepository, InMemoryRepository<Event> {

    Map<Long, Event> events = new ConcurrentHashMap<>();

    @Override
    public void save(Event event) {
        updateTimestampsAndVersion(event);
        events.put(event.getId(), event);
    }

    @Override
    public Set<Event> findAllByEventStatus(EventStatus eventStatus) {
        return events.values().stream()
                .filter(e -> e.getEventStatus().equals(eventStatus))
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteAllByEventStatus(EventStatus eventStatus) {
        events.entrySet().removeIf(e -> e.getValue().getEventStatus().equals(eventStatus));
    }
}

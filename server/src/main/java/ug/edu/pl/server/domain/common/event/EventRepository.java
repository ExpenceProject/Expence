package ug.edu.pl.server.domain.common.event;

import org.springframework.data.repository.Repository;

import java.util.Set;

interface EventRepository extends Repository<Event, Long> {
    void save(Event event);

    Set<Event> findAllByEventStatus(EventStatus eventStatus);

    void deleteAllByEventStatus(EventStatus eventStatus);
}

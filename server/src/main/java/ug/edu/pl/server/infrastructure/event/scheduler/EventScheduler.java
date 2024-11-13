package ug.edu.pl.server.infrastructure.event.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import ug.edu.pl.server.domain.common.event.EventFacade;

class EventScheduler {

    private final EventFacade eventFacade;

    EventScheduler(EventFacade eventFacade) {
        this.eventFacade = eventFacade;
    }

    @Scheduled(fixedRateString = "${scheduler.event.publish}")
    void publishAllEventsPeriodically() {
        eventFacade.publishAllPendingEvents();
    }

    @Scheduled(fixedRateString = "${scheduler.event.clean-up}")
    void cleanUpPublishedEventsPeriodically() {
        eventFacade.cleanUpPublishedEvents();
    }
}

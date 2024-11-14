package ug.edu.pl.server.infrastructure.event.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ug.edu.pl.server.domain.common.event.EventFacade;

@EnableScheduling
@Configuration
class EventSchedulerConfiguration {

    @Bean
    EventScheduler eventScheduler(EventFacade eventFacade) {
        return new EventScheduler(eventFacade);
    }
}

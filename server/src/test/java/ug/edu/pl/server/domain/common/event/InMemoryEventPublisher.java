package ug.edu.pl.server.domain.common.event;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

class InMemoryEventPublisher implements ApplicationEventPublisher {

    @Override
    public void publishEvent(@NotNull Object event) {
    }
}

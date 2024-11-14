package ug.edu.pl.server.infrastructure.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ug.edu.pl.server.base.IntegrationTest;
import ug.edu.pl.server.domain.common.event.TestDomainEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class DomainEventListenerTest extends IntegrationTest {

    @Autowired
    ApplicationEventPublisher publisher;

    @Autowired
    TestEventListener testEventListener;

    @Test
    void shouldListenForDomainEvents() {
        // given & when
        publisher.publishEvent(new TestDomainEvent());

        // then
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(testEventListener.events).hasSize(1));
    }
}

@Component
class TestEventListener {

    List<TestDomainEvent> events = new ArrayList<>();

    @DomainEventListener
    void handle(TestDomainEvent testDomainEvent) {
        events.add(testDomainEvent);
    }
}

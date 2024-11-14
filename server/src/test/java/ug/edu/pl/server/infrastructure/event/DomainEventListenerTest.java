package ug.edu.pl.server.infrastructure.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ug.edu.pl.server.base.IntegrationTest;
import ug.edu.pl.server.domain.common.event.TestDomainEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class DomainEventListenerTest extends IntegrationTest {

    @Autowired
    ApplicationEventPublisher publisher;

    @SpyBean
    TestEventListener testEventListener;

    @Test
    void shouldListenForDomainEvents() {
        // given & when
        publisher.publishEvent(new TestDomainEvent());

        // then
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(testEventListener, times(1)).handle(any(TestDomainEvent.class)));
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

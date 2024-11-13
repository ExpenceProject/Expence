package ug.edu.pl.server.infrastructure.event.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import ug.edu.pl.server.base.IntegrationTest;

import java.time.Duration;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class EventSchedulerTest extends IntegrationTest {

    @SpyBean
    private EventScheduler eventScheduler;

    @Test
    void shouldRunPublishAllPendingEventsExactlyGivenTimes() {
        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(eventScheduler, atLeast(1)).publishAllEventsPeriodically());
    }

    @Test
    void shouldRunCleanUpPublishedEventsExactlyGivenTimes() {
        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(eventScheduler, atLeast(1)).cleanUpPublishedEventsPeriodically());
    }
}

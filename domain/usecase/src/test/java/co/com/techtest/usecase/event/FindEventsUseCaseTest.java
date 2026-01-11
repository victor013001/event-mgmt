package co.com.techtest.usecase.event;

import co.com.techtest.model.event.Event;
import co.com.techtest.model.event.gateway.EventGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindEventsUseCaseTest {

    @Mock
    private EventGateway eventGateway;

    private FindEventsUseCase findEventsUseCase;

    @BeforeEach
    void setUp() {
        findEventsUseCase = new FindEventsUseCase(eventGateway);
    }

    @Test
    void shouldFindEventsByPlaceSuccessfully() {
        List<Event> events = List.of(
                new Event("event-1", "Event 1", LocalDateTime.now(), "Test Place", 100L, "user1", System.currentTimeMillis()),
                new Event("event-2", "Event 2", LocalDateTime.now(), "Test Place", 200L, "user2", System.currentTimeMillis())
        );

        when(eventGateway.findEventsByPlace("Test Place")).thenReturn(Mono.just(events));

        Mono<List<Event>> result = findEventsUseCase.findEventsByPlace("Test Place");

        StepVerifier.create(result)
                .expectNextMatches(eventList -> {
                    assertEquals(2, eventList.size());
                    assertEquals("Test Place", eventList.get(0).place());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyListWhenNoEventsFound() {
        when(eventGateway.findEventsByPlace("Nonexistent Place")).thenReturn(Mono.just(List.of()));

        Mono<List<Event>> result = findEventsUseCase.findEventsByPlace("Nonexistent Place");

        StepVerifier.create(result)
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }
}

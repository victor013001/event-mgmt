package co.com.techtest.dynamodb.event;

import co.com.techtest.dynamodb.event.repository.EventRepository;
import co.com.techtest.model.event.Event;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventDynamoAdapterTest {

    @Mock
    private EventRepository eventRepository;

    private EventDynamoAdapter eventDynamoAdapter;

    @BeforeEach
    void setUp() {
        eventDynamoAdapter = new EventDynamoAdapter(eventRepository);
    }

    @Test
    void shouldSaveEventSuccessfully() {
        Event event = new Event(
                "event-123",
                "Test Event",
                LocalDateTime.now(),
                "Test Place",
                100L,
                "user123",
                System.currentTimeMillis()
        );

        when(eventRepository.save(any(Event.class))).thenReturn(Mono.just(event));

        Mono<Event> result = eventDynamoAdapter.saveEvent(event);

        StepVerifier.create(result)
                .expectNextMatches(savedEvent -> {
                    assertEquals("event-123", savedEvent.id());
                    assertEquals("Test Event", savedEvent.name());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleDynamoDbException() {
        Event event = new Event(
                "event-123",
                "Test Event",
                LocalDateTime.now(),
                "Test Place",
                100L,
                "user123",
                System.currentTimeMillis()
        );

        when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.error(DynamoDbException.builder().message("DynamoDB error").build()));

        Mono<Event> result = eventDynamoAdapter.saveEvent(event);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof TechnicalException &&
                        ((TechnicalException) throwable).getTechnicalMessage() == TechnicalMessageType.ERROR_MS_DYNAMO_ERROR)
                .verify();
    }

    @Test
    void shouldFindEventsByPlaceSuccessfully() {
        List<Event> events = List.of(
                new Event("event-1", "Event 1", LocalDateTime.now(), "Test Place", 100L, "user1", System.currentTimeMillis()),
                new Event("event-2", "Event 2", LocalDateTime.now(), "Test Place", 200L, "user2", System.currentTimeMillis())
        );

        when(eventRepository.getAllEventsByPlace("Test Place")).thenReturn(Mono.just(events));

        Mono<List<Event>> result = eventDynamoAdapter.findEventsByPlace("Test Place");

        StepVerifier.create(result)
                .expectNextMatches(eventList -> {
                    assertEquals(2, eventList.size());
                    assertEquals("Test Place", eventList.get(0).place());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldDeleteEventSuccessfully() {
        Event event = new Event("event-123", "Test Event", LocalDateTime.now(), "Test Place", 100L, "user123", System.currentTimeMillis());

        when(eventRepository.delete(event)).thenReturn(Mono.just(event));

        Mono<Event> result = eventDynamoAdapter.deleteEvent(event);

        StepVerifier.create(result)
                .expectNext(event)
                .verifyComplete();

        verify(eventRepository).delete(event);
    }

    @Test
    void shouldHandleDynamoDbExceptionInDeleteEvent() {
        Event event = new Event("event-123", "Test Event", LocalDateTime.now(), "Test Place", 100L, "user123", System.currentTimeMillis());

        when(eventRepository.delete(event))
                .thenReturn(Mono.error(DynamoDbException.builder().message("DynamoDB error").build()));

        Mono<Event> result = eventDynamoAdapter.deleteEvent(event);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof TechnicalException &&
                        ((TechnicalException) throwable).getTechnicalMessage() == TechnicalMessageType.ERROR_MS_DYNAMO_ERROR)
                .verify();
    }

    @Test
    void shouldHandleDynamoDbExceptionInFindEventsByPlace() {
        when(eventRepository.getAllEventsByPlace("Test Place"))
                .thenReturn(Mono.error(DynamoDbException.builder().message("DynamoDB error").build()));

        Mono<List<Event>> result = eventDynamoAdapter.findEventsByPlace("Test Place");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof TechnicalException &&
                        ((TechnicalException) throwable).getTechnicalMessage() == TechnicalMessageType.ERROR_MS_DYNAMO_ERROR)
                .verify();
    }
}

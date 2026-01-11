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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        Event event = Event.builder()
                .id("event-123")
                .name("Test Event")
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(100L)
                .build();

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
        Event event = Event.builder()
                .id("event-123")
                .name("Test Event")
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(100L)
                .build();

        when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.error(DynamoDbException.builder().message("DynamoDB error").build()));

        Mono<Event> result = eventDynamoAdapter.saveEvent(event);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof TechnicalException &&
                        ((TechnicalException) throwable).getTechnicalMessage() == TechnicalMessageType.ERROR_MS_DYNAMO_ERROR)
                .verify();
    }
}

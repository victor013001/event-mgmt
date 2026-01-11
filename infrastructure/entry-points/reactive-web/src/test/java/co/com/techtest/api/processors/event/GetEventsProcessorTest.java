package co.com.techtest.api.processors.event;

import co.com.techtest.api.dto.request.event.GetEventRequestParams;
import co.com.techtest.model.event.Event;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.usecase.event.FindEventsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEventsProcessorTest {

    @Mock
    private FindEventsUseCase findEventsUseCase;

    private GetEventsProcessor getEventsProcessor;

    @BeforeEach
    void setUp() {
        getEventsProcessor = new GetEventsProcessor(findEventsUseCase);
    }

    @Test
    void shouldExecuteSuccessfullyWithPlace() {
        List<Event> events = List.of(
                new Event("event-1", "Event 1", LocalDateTime.now(), "Test Place", 100L, "user1", System.currentTimeMillis())
        );

        GetEventRequestParams params = new GetEventRequestParams("flow123", "user123", "Test Place");

        when(findEventsUseCase.findEventsByPlace("Test Place")).thenReturn(Mono.just(events));

        Mono<ServerResponse> result = getEventsProcessor.execute(params, OperationType.GET_EVENTS);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldExecuteSuccessfullyWithNullPlace() {
        List<Event> events = List.of();

        GetEventRequestParams params = new GetEventRequestParams("flow123", "user123", "Valid Place");

        when(findEventsUseCase.findEventsByPlace("Valid Place")).thenReturn(Mono.just(events));

        Mono<ServerResponse> result = getEventsProcessor.execute(params, OperationType.GET_EVENTS);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldReturnBadRequestForInvalidPlace() {
        GetEventRequestParams params = new GetEventRequestParams("flow123", "user123", null);

        Mono<ServerResponse> result = getEventsProcessor.execute(params, OperationType.GET_EVENTS);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleException() {
        GetEventRequestParams params = new GetEventRequestParams("flow123", "user123", "Valid Place");

        when(findEventsUseCase.findEventsByPlace("Valid Place"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ServerResponse> result = getEventsProcessor.execute(params, OperationType.GET_EVENTS);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}

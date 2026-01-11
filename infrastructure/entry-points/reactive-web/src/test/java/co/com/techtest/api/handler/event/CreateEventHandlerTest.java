package co.com.techtest.api.handler.event;

import co.com.techtest.api.dto.request.event.CreateEventRequest;
import co.com.techtest.api.processors.CreateEventProcessor;
import co.com.techtest.model.util.enums.OperationType;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateEventHandlerTest {

    @Mock
    private CreateEventProcessor createEventProcessor;

    private CreateEventHandler createEventHandler;

    @BeforeEach
    void setUp() {
        createEventHandler = new CreateEventHandler(createEventProcessor);
    }

    @Test
    void shouldHandleValidRequest() {
        CreateEventRequest request = CreateEventRequest.builder()
                .name("Test Event")
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(100L)
                .build();

        ServerRequest serverRequest = MockServerRequest.builder()
                .header("x-user-id", "user123")
                .header("flow-id", "flow123")
                .body(Mono.just(request));

        when(createEventProcessor.execute(any(CreateEventRequest.class), eq(OperationType.CREATE_EVENT)))
                .thenReturn(ServerResponse.ok().build());

        Mono<ServerResponse> result = createEventHandler.handle(serverRequest);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleFallbackWithException() {
        ServerRequest serverRequest = MockServerRequest.builder().build();
        Exception exception = new RuntimeException("Test exception");

        Mono<ServerResponse> result = createEventHandler.fallback(serverRequest, exception);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleFallbackWithCallNotPermittedException() {
        ServerRequest serverRequest = MockServerRequest.builder().build();
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("createEvent");
        CallNotPermittedException exception = CallNotPermittedException.createCallNotPermittedException(circuitBreaker);

        Mono<ServerResponse> result = createEventHandler.fallback(serverRequest, exception);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }
}
package co.com.techtest.api.handler.inventory;

import co.com.techtest.api.processors.inventory.GetInventoryProcessor;
import co.com.techtest.model.util.enums.OperationType;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEventAvailabilityHandlerTest {

    @Mock
    private GetInventoryProcessor getInventoryProcessor;

    private GetEventAvailabilityHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetEventAvailabilityHandler(getInventoryProcessor);
    }

    @Test
    void shouldHandleRequestSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("eventId", "event-123")
                .header("flow-id", "flow-123")
                .header("x-user-id", "user-123")
                .build();

        when(getInventoryProcessor.execute(any(), eq(OperationType.GET_EVENT_AVAILABILITY)))
                .thenReturn(ServerResponse.ok().build());

        Mono<ServerResponse> result = handler.handle(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(getInventoryProcessor).execute(any(), eq(OperationType.GET_EVENT_AVAILABILITY));
    }

    @Test
    void shouldHandleFallbackWithException() {
        ServerRequest request = MockServerRequest.builder().build();
        Exception exception = new RuntimeException("Test error");

        Mono<ServerResponse> result = handler.fallback(request, exception);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldHandleFallbackWithCallNotPermittedException() {
        ServerRequest request = MockServerRequest.builder().build();
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("circuit-breaker");
        CallNotPermittedException exception = CallNotPermittedException.createCallNotPermittedException(circuitBreaker);

        Mono<ServerResponse> result = handler.fallback(request, exception);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldHandleRequestWithMissingPathVariable() {
        ServerRequest request = MockServerRequest.builder()
                .header("flow-id", "flow-123")
                .header("x-user-id", "user-123")
                .build();

        when(getInventoryProcessor.execute(any(), eq(OperationType.GET_EVENT_AVAILABILITY)))
                .thenReturn(ServerResponse.ok().build());

        Mono<ServerResponse> result = handler.handle(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }
}
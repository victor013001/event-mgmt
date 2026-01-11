package co.com.techtest.api.handler.ticket;

import co.com.techtest.api.processors.ticket.GetTicketProcessor;
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
class GetTicketHandlerTest {

    @Mock
    private GetTicketProcessor getTicketProcessor;

    private GetTicketHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetTicketHandler(getTicketProcessor);
    }

    @Test
    void shouldHandleRequestSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("ticketId", "ticket-123")
                .header("flow-id", "flow-123")
                .header("x-user-id", "user-123")
                .build();

        when(getTicketProcessor.execute(any(), eq(OperationType.GET_TICKET)))
                .thenReturn(ServerResponse.ok().build());

        Mono<ServerResponse> result = handler.handle(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(getTicketProcessor).execute(any(), eq(OperationType.GET_TICKET));
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
}
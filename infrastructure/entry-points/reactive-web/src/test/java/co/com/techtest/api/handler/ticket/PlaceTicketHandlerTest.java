package co.com.techtest.api.handler.ticket;

import co.com.techtest.api.dto.request.ticket.PlaceTicketRequest;
import co.com.techtest.api.processors.ticket.PlaceTicketProcessor;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceTicketHandlerTest {

    @Mock
    private PlaceTicketProcessor placeTicketProcessor;

    private PlaceTicketHandler placeTicketHandler;

    @BeforeEach
    void setUp() {
        placeTicketHandler = new PlaceTicketHandler(placeTicketProcessor);
    }

    @Test
    void shouldHandleValidRequest() {
        PlaceTicketRequest request = new PlaceTicketRequest(2);

        ServerRequest serverRequest = MockServerRequest.builder()
                .header("x-user-id", "user123")
                .header("flow-id", "flow123")
                .pathVariable("eventId", "event123")
                .body(Mono.just(request));

        when(placeTicketProcessor.execute(any(), eq(OperationType.PLACE_EVENT_TICKET)))
                .thenReturn(ServerResponse.ok().build());

        Mono<ServerResponse> result = placeTicketHandler.handle(serverRequest);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleFallbackWithException() {
        ServerRequest serverRequest = MockServerRequest.builder().build();
        Exception exception = new RuntimeException("Test exception");

        Mono<ServerResponse> result = placeTicketHandler.fallback(serverRequest, exception);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandlePathVariableExtractionError() {
        PlaceTicketRequest request = new PlaceTicketRequest(2);

        ServerRequest serverRequest = MockServerRequest.builder()
                .header("x-user-id", "user123")
                .header("flow-id", "flow123")
                .body(Mono.just(request));

        when(placeTicketProcessor.execute(any(), eq(OperationType.PLACE_EVENT_TICKET)))
                .thenReturn(ServerResponse.ok().build());

        Mono<ServerResponse> result = placeTicketHandler.handle(serverRequest);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleFallbackWithCallNotPermittedException() {
        ServerRequest serverRequest = MockServerRequest.builder().build();
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("placeTicket");
        CallNotPermittedException exception = CallNotPermittedException.createCallNotPermittedException(circuitBreaker);

        Mono<ServerResponse> result = placeTicketHandler.fallback(serverRequest, exception);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }
}

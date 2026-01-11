package co.com.techtest.api.handler.event;

import co.com.techtest.api.dto.request.event.GetEventRequestParams;
import co.com.techtest.api.processors.event.GetEventsProcessor;
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
class GetEventsHandlerTest {

    @Mock
    private GetEventsProcessor getEventsProcessor;

    private GetEventsHandler getEventsHandler;

    @BeforeEach
    void setUp() {
        getEventsHandler = new GetEventsHandler(getEventsProcessor);
    }

    @Test
    void shouldHandleRequestWithPlaceParam() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .queryParam("place", "Test Place")
                .build();

        when(getEventsProcessor.execute(any(GetEventRequestParams.class), eq(OperationType.GET_EVENTS)))
                .thenReturn(ServerResponse.ok().build());

        Mono<ServerResponse> result = getEventsHandler.handle(serverRequest);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleFallbackWithCallNotPermittedException() {
        ServerRequest serverRequest = MockServerRequest.builder().build();
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("getEvents");
        CallNotPermittedException exception = CallNotPermittedException.createCallNotPermittedException(circuitBreaker);

        Mono<ServerResponse> result = getEventsHandler.fallback(serverRequest, exception);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleFallback() {
        ServerRequest serverRequest = MockServerRequest.builder().build();
        Exception exception = new RuntimeException("Test exception");

        Mono<ServerResponse> result = getEventsHandler.fallback(serverRequest, exception);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }
}

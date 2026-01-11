package co.com.techtest.api.processors;

import co.com.techtest.api.dto.request.event.CreateEventRequest;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.orchestrator.CreateEventOrchestrator;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateEventProcessorTest {

    @Mock
    private CreateEventOrchestrator createEventOrchestrator;

    private CreateEventProcessor createEventProcessor;

    @BeforeEach
    void setUp() {
        createEventProcessor = new CreateEventProcessor(createEventOrchestrator);
    }

    @Test
    void shouldExecuteSuccessfullyWithValidRequest() {
        CreateEventRequest validRequest = CreateEventRequest.builder()
                .name("Test Event")
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(100L)
                .flowId("flow123")
                .build();

        when(createEventOrchestrator.createEvent(any())).thenReturn(Mono.empty());

        Mono<ServerResponse> result = createEventProcessor.execute(validRequest, OperationType.CREATE_EVENT);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldReturnBadRequestWithInvalidRequest() {
        CreateEventRequest invalidRequest = CreateEventRequest.builder()
                .name(null)
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(100L)
                .flowId("flow123")
                .build();

        Mono<ServerResponse> result = createEventProcessor.execute(invalidRequest, OperationType.CREATE_EVENT);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleBusinessException() {
        CreateEventRequest validRequest = CreateEventRequest.builder()
                .name("Test Event")
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(100L)
                .flowId("flow123")
                .build();

        when(createEventOrchestrator.createEvent(any())).thenReturn(Mono.error(new BusinessException(TechnicalMessageType.ERROR_MS_INTERNAL_SERVER)));

        Mono<ServerResponse> result = createEventProcessor.execute(validRequest, OperationType.CREATE_EVENT);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }
}

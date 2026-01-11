package co.com.techtest.api.processors.ticket;

import co.com.techtest.api.dto.request.ticket.PlaceTicketReqParams;
import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.orchestrator.TicketOrchestrator;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceTicketProcessorTest {

    @Mock
    private TicketOrchestrator ticketOrchestrator;

    private PlaceTicketProcessor placeTicketProcessor;

    @BeforeEach
    void setUp() {
        placeTicketProcessor = new PlaceTicketProcessor(ticketOrchestrator);
    }

    @Test
    void shouldExecuteSuccessfullyWithValidRequest() {
        PlaceTicketReqParams validRequest = PlaceTicketReqParams.builder()
                .eventId("event123")
                .quantity(2)
                .xUserId("user123")
                .flowId("flow123")
                .build();

        Ticket ticket = new Ticket(
                "ticket123",
                "event123",
                "user123",
                2,
                TicketStatus.RESERVED,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );

        when(ticketOrchestrator.placeTicket(any())).thenReturn(Mono.just(ticket));

        Mono<ServerResponse> result = placeTicketProcessor.execute(validRequest, OperationType.PLACE_EVENT_TICKET);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldReturnBadRequestWithInvalidRequest() {
        PlaceTicketReqParams invalidRequest = PlaceTicketReqParams.builder()
                .eventId(null)
                .quantity(2)
                .xUserId("user123")
                .flowId("flow123")
                .build();

        Mono<ServerResponse> result = placeTicketProcessor.execute(invalidRequest, OperationType.PLACE_EVENT_TICKET);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleBusinessException() {
        PlaceTicketReqParams validRequest = PlaceTicketReqParams.builder()
                .eventId("event123")
                .quantity(2)
                .xUserId("user123")
                .flowId("flow123")
                .build();

        when(ticketOrchestrator.placeTicket(any()))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessageType.ERROR_MS_INTERNAL_SERVER)));

        Mono<ServerResponse> result = placeTicketProcessor.execute(validRequest, OperationType.PLACE_EVENT_TICKET);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }
}

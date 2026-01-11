package co.com.techtest.api.processors.ticket;

import co.com.techtest.api.dto.request.ticket.GetTicketRequestParams;
import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.ticket.GetTicketUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTicketProcessorTest {

    @Mock
    private GetTicketUseCase getTicketUseCase;

    private GetTicketProcessor getTicketProcessor;

    @BeforeEach
    void setUp() {
        getTicketProcessor = new GetTicketProcessor(getTicketUseCase);
    }

    @Test
    void shouldExecuteSuccessfully() {
        Ticket ticket = new Ticket(
                "ticket-123",
                "event-123",
                "user-123",
                2,
                TicketStatus.RESERVED,
                null,
                null,
                null
        );

        GetTicketRequestParams params = new GetTicketRequestParams("flow123", "user123", "ticket-123");

        when(getTicketUseCase.getTicketById("ticket-123", "user123")).thenReturn(Mono.just(ticket));

        Mono<ServerResponse> result = getTicketProcessor.execute(params, OperationType.GET_TICKET);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleTicketNotFound() {
        GetTicketRequestParams params = new GetTicketRequestParams("flow123", "user123", "ticket-123");
        BusinessException businessException = new BusinessException(TechnicalMessageType.ERROR_TICKET_NOT_FOUND);

        when(getTicketUseCase.getTicketById("ticket-123", "user123"))
                .thenReturn(Mono.error(businessException));

        Mono<ServerResponse> result = getTicketProcessor.execute(params, OperationType.GET_TICKET);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.NOT_FOUND, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleAccessDenied() {
        GetTicketRequestParams params = new GetTicketRequestParams("flow123", "user123", "ticket-123");
        BusinessException businessException = new BusinessException(TechnicalMessageType.ERROR_TICKET_ACCESS_DENIED);

        when(getTicketUseCase.getTicketById("ticket-123", "user123"))
                .thenReturn(Mono.error(businessException));

        Mono<ServerResponse> result = getTicketProcessor.execute(params, OperationType.GET_TICKET);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.FORBIDDEN, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void shouldHandleInternalServerError() {
        GetTicketRequestParams params = new GetTicketRequestParams("flow123", "user123", "ticket-123");
        BusinessException businessException = new BusinessException(TechnicalMessageType.ERROR_MS_INTERNAL_SERVER);

        when(getTicketUseCase.getTicketById("ticket-123", "user123"))
                .thenReturn(Mono.error(businessException));

        Mono<ServerResponse> result = getTicketProcessor.execute(params, OperationType.GET_TICKET);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }
}

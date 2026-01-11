package co.com.techtest.usecase.ticket;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTicketUseCaseTest {

    @Mock
    private TicketGateway ticketGateway;

    private GetTicketUseCase getTicketUseCase;

    @BeforeEach
    void setUp() {
        getTicketUseCase = new GetTicketUseCase(ticketGateway);
    }

    @Test
    void shouldReturnTicketWhenExistsAndBelongsToUser() {
        String ticketId = "ticket-123";
        String userId = "user-123";
        Ticket ticket = new Ticket(ticketId, "event-123", userId, 1, null, null, null, null);

        when(ticketGateway.getTicketById(ticketId)).thenReturn(Mono.just(ticket));

        StepVerifier.create(getTicketUseCase.getTicketById(ticketId, userId))
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        String ticketId = "ticket-123";
        String userId = "user-123";

        when(ticketGateway.getTicketById(ticketId)).thenReturn(Mono.empty());

        StepVerifier.create(getTicketUseCase.getTicketById(ticketId, userId))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessageType.ERROR_TICKET_NOT_FOUND)
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenTicketBelongsToAnotherUser() {
        String ticketId = "ticket-123";
        String userId = "user-123";
        String anotherUserId = "user-456";
        Ticket ticket = new Ticket(ticketId, "event-123", anotherUserId, 1, null, null, null, null);

        when(ticketGateway.getTicketById(ticketId)).thenReturn(Mono.just(ticket));

        StepVerifier.create(getTicketUseCase.getTicketById(ticketId, userId))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessageType.ERROR_TICKET_ACCESS_DENIED)
                .verify();
    }
}

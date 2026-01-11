package co.com.techtest.usecase.orchestrator;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketStatusEvent;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.usecase.ticket.FindTicketsByStatusUseCase;
import co.com.techtest.usecase.ticket.UpdateTicketUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketExpirationOrchestratorTest {

    @Mock
    private FindTicketsByStatusUseCase findTicketsByStatusUseCase;

    @Mock
    private UpdateTicketUseCase updateTicketUseCase;

    private TicketExpirationOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new TicketExpirationOrchestrator(findTicketsByStatusUseCase, updateTicketUseCase);
    }

    @Test
    void shouldExpireReservedTicketsSuccessfully() {
        Ticket expiredTicket = createExpiredTicket();

        when(findTicketsByStatusUseCase.findByStatus(TicketStatus.RESERVED))
                .thenReturn(Flux.just(expiredTicket));
        when(updateTicketUseCase.updateTicketStatus(expiredTicket.ticketId(), TicketStatus.EXPIRED))
                .thenReturn(Mono.just(expiredTicket));

        Mono<Void> result = orchestrator.expireReservedTickets();

        StepVerifier.create(result)
                .verifyComplete();

        verify(findTicketsByStatusUseCase).findByStatus(TicketStatus.RESERVED);
        verify(updateTicketUseCase).updateTicketStatus(expiredTicket.ticketId(), TicketStatus.EXPIRED);
    }

    @Test
    void shouldNotExpireNonExpiredTickets() {
        Ticket validTicket = createValidTicket();

        when(findTicketsByStatusUseCase.findByStatus(TicketStatus.RESERVED))
                .thenReturn(Flux.just(validTicket));

        Mono<Void> result = orchestrator.expireReservedTickets();

        StepVerifier.create(result)
                .verifyComplete();

        verify(findTicketsByStatusUseCase).findByStatus(TicketStatus.RESERVED);
    }

    @Test
    void shouldHandleUpdateErrorsGracefully() {
        Ticket expiredTicket = createExpiredTicket();

        when(findTicketsByStatusUseCase.findByStatus(TicketStatus.RESERVED))
                .thenReturn(Flux.just(expiredTicket));
        when(updateTicketUseCase.updateTicketStatus(expiredTicket.ticketId(), TicketStatus.EXPIRED))
                .thenReturn(Mono.error(new RuntimeException("Update failed")));

        Mono<Void> result = orchestrator.expireReservedTickets();

        StepVerifier.create(result)
                .verifyComplete();

        verify(updateTicketUseCase).updateTicketStatus(expiredTicket.ticketId(), TicketStatus.EXPIRED);
    }

    @Test
    void shouldProcessMultipleExpiredTickets() {
        Ticket expiredTicket1 = createExpiredTicketWithId("ticket-123");
        Ticket expiredTicket2 = createExpiredTicketWithId("ticket-456");

        when(findTicketsByStatusUseCase.findByStatus(TicketStatus.RESERVED))
                .thenReturn(Flux.just(expiredTicket1, expiredTicket2));
        when(updateTicketUseCase.updateTicketStatus(any(), eq(TicketStatus.EXPIRED)))
                .thenReturn(Mono.just(expiredTicket1));

        Mono<Void> result = orchestrator.expireReservedTickets();

        StepVerifier.create(result)
                .verifyComplete();

        verify(updateTicketUseCase, times(2)).updateTicketStatus(any(), eq(TicketStatus.EXPIRED));
    }

    private Ticket createExpiredTicket() {
        return createExpiredTicketWithId("ticket-123");
    }

    private Ticket createExpiredTicketWithId(String ticketId) {
        return new Ticket(
                ticketId,
                "event-123",
                "user-123",
                2,
                TicketStatus.RESERVED,
                List.of(new TicketStatusEvent(TicketStatus.RESERVED, System.currentTimeMillis())),
                LocalDateTime.now().minusMinutes(15),
                LocalDateTime.now().minusMinutes(5)
        );
    }

    private Ticket createValidTicket() {
        return new Ticket(
                "ticket-456",
                "event-123",
                "user-123",
                2,
                TicketStatus.RESERVED,
                List.of(new TicketStatusEvent(TicketStatus.RESERVED, System.currentTimeMillis())),
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5)
        );
    }
}

package co.com.techtest.usecase.ticket;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketStatusEvent;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.model.util.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTicketUseCaseTest {

    @Mock
    private TicketGateway ticketGateway;

    @InjectMocks
    private UpdateTicketUseCase updateTicketUseCase;

    @Test
    void shouldUpdateTicketStatusToSold() {
        String ticketId = "ticket123";
        List<TicketStatusEvent> statusEvents = new ArrayList<>();
        statusEvents.add(new TicketStatusEvent(TicketStatus.RESERVED, System.currentTimeMillis()));

        Ticket existingTicket = new Ticket(
                ticketId,
                "event123",
                "user123",
                2,
                TicketStatus.RESERVED,
                statusEvents,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5)
        );

        Ticket updatedTicket = new Ticket(
                ticketId,
                "event123",
                "user123",
                2,
                TicketStatus.SOLD,
                statusEvents,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5)
        );

        when(ticketGateway.getTicketById(ticketId)).thenReturn(Mono.just(existingTicket));
        when(ticketGateway.updateTicketSold(any(Ticket.class))).thenReturn(Mono.just(updatedTicket));

        StepVerifier.create(updateTicketUseCase.updateTicketStatus(ticketId, TicketStatus.SOLD))
                .expectNext(updatedTicket)
                .verifyComplete();
    }

    @Test
    void shouldUpdateTicketStatusToExpiredWhenExpired() {
        String ticketId = "ticket123";
        List<TicketStatusEvent> statusEvents = new ArrayList<>();
        statusEvents.add(new TicketStatusEvent(TicketStatus.RESERVED, System.currentTimeMillis()));

        Ticket expiredTicket = new Ticket(
                ticketId,
                "event123",
                "user123",
                2,
                TicketStatus.RESERVED,
                statusEvents,
                LocalDateTime.now().minusMinutes(15),
                LocalDateTime.now().minusMinutes(5)
        );

        Ticket updatedTicket = new Ticket(
                ticketId,
                "event123",
                "user123",
                2,
                TicketStatus.EXPIRED,
                statusEvents,
                LocalDateTime.now().minusMinutes(15),
                LocalDateTime.now().minusMinutes(5)
        );

        when(ticketGateway.getTicketById(ticketId)).thenReturn(Mono.just(expiredTicket));
        when(ticketGateway.updateTicketRelease(any(Ticket.class))).thenReturn(Mono.just(updatedTicket));

        StepVerifier.create(updateTicketUseCase.updateTicketStatus(ticketId, TicketStatus.SOLD))
                .expectNext(updatedTicket)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenTicketNotModifiable() {
        String ticketId = "ticket123";
        Ticket soldTicket = new Ticket(
                ticketId,
                "event123",
                "user123",
                2,
                TicketStatus.SOLD,
                new ArrayList<>(),
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5)
        );

        when(ticketGateway.getTicketById(ticketId)).thenReturn(Mono.just(soldTicket));

        StepVerifier.create(updateTicketUseCase.updateTicketStatus(ticketId, TicketStatus.EXPIRED))
                .expectErrorSatisfies(ex -> {
                    assertEquals(BusinessException.class, ex.getClass());
                    BusinessException be = (BusinessException) ex;
                    assertEquals(TechnicalMessageType.ERROR_MS_NON_MODIFIABLE_TICKET, be.getTechnicalMessage());
                })
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        String ticketId = "nonexistent";

        when(ticketGateway.getTicketById(ticketId)).thenReturn(Mono.empty());

        StepVerifier.create(updateTicketUseCase.updateTicketStatus(ticketId, TicketStatus.SOLD))
                .expectErrorSatisfies(ex -> {
                    assertEquals(BusinessException.class, ex.getClass());
                    BusinessException be = (BusinessException) ex;
                    assertEquals(TechnicalMessageType.ERROR_MS_NON_MODIFIABLE_TICKET, be.getTechnicalMessage());
                })
                .verify();
    }
}

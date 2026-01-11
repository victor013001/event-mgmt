package co.com.techtest.usecase.ticket;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketStatusEvent;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindTicketsByStatusUseCaseTest {

    @Mock
    private TicketGateway ticketGateway;

    private FindTicketsByStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindTicketsByStatusUseCase(ticketGateway);
    }

    @Test
    void shouldFindTicketsByStatus() {
        Ticket ticket = createTicket(TicketStatus.RESERVED);
        when(ticketGateway.findTicketsByStatus(TicketStatus.RESERVED))
                .thenReturn(Flux.just(ticket));

        Flux<Ticket> result = useCase.findByStatus(TicketStatus.RESERVED);

        StepVerifier.create(result)
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoTicketsFound() {
        when(ticketGateway.findTicketsByStatus(TicketStatus.SOLD))
                .thenReturn(Flux.empty());

        Flux<Ticket> result = useCase.findByStatus(TicketStatus.SOLD);

        StepVerifier.create(result)
                .verifyComplete();
    }

    private Ticket createTicket(TicketStatus status) {
        return new Ticket(
                "ticket-123",
                "event-123",
                "user-123",
                2,
                status,
                List.of(new TicketStatusEvent(status, System.currentTimeMillis())),
                LocalDateTime.now().minusMinutes(15),
                LocalDateTime.now().plusMinutes(5)
        );
    }
}

package co.com.techtest.usecase.ticket;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketParameter;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketUseCaseTest {

    @Mock
    private TicketGateway ticketGateway;

    @InjectMocks
    private TicketUseCase ticketUseCase;

    @Test
    void shouldCreateTicketSuccessfully() {
        TicketParameter parameter = new TicketParameter("flow123", "user123", "event123", 2);
        Ticket expectedTicket = new Ticket(
                "ticket123",
                "event123",
                "user123",
                2,
                TicketStatus.RESERVED,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );

        when(ticketGateway.saveTicket(any(Ticket.class))).thenReturn(Mono.just(expectedTicket));

        StepVerifier.create(ticketUseCase.createTicket(parameter))
                .consumeNextWith(ticket -> {
                    assertNotNull(ticket.ticketId());
                    assertEquals(parameter.eventId(), ticket.eventId());
                    assertEquals(parameter.userId(), ticket.userId());
                    assertEquals(parameter.quantity(), ticket.quantity());
                    assertEquals(TicketStatus.RESERVED, ticket.currentStatus());
                    assertNotNull(ticket.createdAt());
                    assertNotNull(ticket.expiresAt());
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagateGatewayError() {
        TicketParameter parameter = new TicketParameter("flow123", "user123", "event123", 2);
        RuntimeException gatewayError = new RuntimeException("Gateway error");

        when(ticketGateway.saveTicket(any(Ticket.class))).thenReturn(Mono.error(gatewayError));

        StepVerifier.create(ticketUseCase.createTicket(parameter))
                .expectError(RuntimeException.class)
                .verify();
    }
}

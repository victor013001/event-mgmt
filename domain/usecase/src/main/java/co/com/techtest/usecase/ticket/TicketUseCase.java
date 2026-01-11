package co.com.techtest.usecase.ticket;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketParameter;
import co.com.techtest.model.ticket.TicketStatusEvent;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class TicketUseCase {

    private final TicketGateway ticketGateway;

    public Mono<Ticket> createTicket(TicketParameter parameter) {
        return ticketGateway.saveTicket(buildNewTicket(parameter));
    }

    private Ticket buildNewTicket(TicketParameter parameter) {
        String ticketId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(10);
        long timestamp = System.currentTimeMillis();
        List<TicketStatusEvent> statusEvents = new ArrayList<>();
        statusEvents.add(new TicketStatusEvent(TicketStatus.RESERVED, timestamp));
        return new Ticket(
                ticketId,
                parameter.eventId(),
                parameter.userId(),
                parameter.quantity(),
                TicketStatus.RESERVED,
                statusEvents,
                now,
                expiresAt
        );
    }
}

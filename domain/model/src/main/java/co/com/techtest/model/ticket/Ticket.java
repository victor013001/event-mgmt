package co.com.techtest.model.ticket;

import co.com.techtest.model.util.enums.ticket.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;

public record Ticket(
        String ticketId,
        String eventId,
        String userId,
        Integer quantity,
        TicketStatus currentStatus,
        List<TicketStatusEvent> ticketStatusEvents,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}

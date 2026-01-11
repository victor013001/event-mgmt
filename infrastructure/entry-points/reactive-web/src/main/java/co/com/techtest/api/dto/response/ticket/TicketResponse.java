package co.com.techtest.api.dto.response.ticket;

import lombok.Builder;

@Builder(toBuilder = true)
public record TicketResponse(
        String ticketId,
        String eventId,
        Integer quantity,
        String status,
        String createdAt,
        String expiresAt
) {
}
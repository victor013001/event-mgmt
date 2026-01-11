package co.com.techtest.api.dto.request.ticket;

import lombok.Builder;

@Builder(toBuilder = true)
public record PlaceTicketReqParams(String flowId, String xUserId, String eventId, Integer quantity) {
}
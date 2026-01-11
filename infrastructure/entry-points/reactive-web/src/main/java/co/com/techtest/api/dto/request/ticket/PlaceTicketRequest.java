package co.com.techtest.api.dto.request.ticket;

import lombok.Builder;

@Builder(toBuilder = true)
public record PlaceTicketRequest(Integer quantity) {
}
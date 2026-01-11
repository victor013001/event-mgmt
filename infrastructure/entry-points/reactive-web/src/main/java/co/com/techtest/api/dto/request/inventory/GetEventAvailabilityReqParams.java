package co.com.techtest.api.dto.request.inventory;

import lombok.Builder;

@Builder(toBuilder = true)
public record GetEventAvailabilityReqParams(String flowId, String xUserId, String eventId) {
}

package co.com.techtest.api.dto.request.event;

import lombok.Builder;

@Builder(toBuilder = true)
public record GetEventRequestParams(String flowId, String xUserId, String place) {
}

package co.com.techtest.api.dto.request.standardstructure;

import lombok.Builder;

@Builder(toBuilder = true)
public record HeaderRequest(String xUserId, String flowId) {
}

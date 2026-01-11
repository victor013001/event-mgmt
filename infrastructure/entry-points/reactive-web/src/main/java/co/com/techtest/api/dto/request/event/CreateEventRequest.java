package co.com.techtest.api.dto.request.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateEventRequest(String xUserId, String flowId, String name, LocalDateTime date,
                                 String place, Long capacity) {
}

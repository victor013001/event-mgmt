package co.com.techtest.model.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record EventParameter(String name, LocalDateTime date, String place, Long capacity, String userId) {
}

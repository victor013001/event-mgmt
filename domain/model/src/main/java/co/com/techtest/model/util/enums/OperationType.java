package co.com.techtest.model.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationType {
    CREATE_EVENT("createEvent", "/api/v1/event", "Create Event Request", "createEventRQ", "Create Event Response", "createEventRS"),
    GET_EVENTS("getEvent", "/api/v1/event", "Get Events Request", "getEventsRQ", "Get Event Response", "getEventsRS"),
    GET_EVENT_AVAILABILITY("getEventAvailability", "/api/v1/event/{eventId}/availability", "Get Event Availability Request", "getEventAvailabilityRQ",
            "Get Event Availability Response", "getEventAvailabilityRS");

    private final String name;
    private final String path;
    private final String nameRequest;
    private final String kvRequest;
    private final String nameResponse;
    private final String kvResponse;
}

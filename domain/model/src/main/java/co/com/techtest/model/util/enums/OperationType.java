package co.com.techtest.model.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationType {
    CREATE_EVENT("createEvent", "/api/v1/event", "Create Event Request", "createEventRQ", "Create Event Response", "createEventRS"),
    GET_EVENTS("getEvent", "/api/v1/event", "Get Events Request", "getEventsRQ", "Get Event Response", "getEventsRS"),
    GET_EVENT_AVAILABILITY("getEventAvailability", "/api/v1/event/{eventId}/availability", "Get Event Availability Request", "getEventAvailabilityRQ",
            "Get Event Availability Response", "getEventAvailabilityRS"),
    PLACE_EVENT_TICKET("placeEventTicket", "/api/v1/event/{eventId}/ticket", "Place Event Ticket Request", "placeEventTicketRQ",
            "Place Event Ticket Response", "placeEventTicketRS"),
    GET_TICKET("getTicket", "/api/v1/ticket/{ticketId}", "Get Ticket Request", "getTicketRQ",
            "Get Ticket Response", "getTicketRS"),
    PROCESS_PURCHASE("processPurchase", "", "Process Purchase Request", "processPurchaseRQ",
            "Process Purchase Response", "processPurchaseRS");

    private final String name;
    private final String path;
    private final String nameRequest;
    private final String kvRequest;
    private final String nameResponse;
    private final String kvResponse;
}

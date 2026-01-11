package co.com.techtest.model.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalMessageType {

    //VALUES
    ERROR_MS_INVALID_NAME("VAL001", 400, "The name is required.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_INVALID_DATE("VAL002", 400, "The date is required.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_INVALID_PLACE("VAL003", 400, "The place is required.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_INVALID_CAPACITY("VAL004", 400, "The capacity has to be greater than 0.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_INVALID_FLOW_ID("VAL005", 400, "The flow id is required.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_INVALID_X_USER_ID("VAL006", 400, "The x user id is required.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_INVALID_EVENT("VAL007", 400, "The event is not valid.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_INVALID_HEADERS("H-001", 400, "The headers are invalid.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_INVALID_EVENT_ID("VAL008", 400, "The event id is required.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_EVENT_NOT_FOUND("VAL009", 404, "The event was not found.", TechnicalMessageType.NOT_FOUND_MSG),
    ERROR_MS_INSUFFICIENT_INVENTORY("VAL010", 400, "Insufficient inventory available.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_INVALID_QUANTITY("VAL011", 400, "The quantity must be greater than 0.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_MS_NON_MODIFIABLE_TICKET("VAL012", 400, "The ticket status cannot be modified.", TechnicalMessageType.BAD_REQUEST_MSG),
    ERROR_TICKET_NOT_FOUND("VAL013", 404, "The ticket was not found.", TechnicalMessageType.NOT_FOUND_MSG),
    ERROR_TICKET_ACCESS_DENIED("VAL014", 403, "Access denied to this ticket.", TechnicalMessageType.FORBIDDEN_MSG),
    ERROR_MS_INVALID_TICKET_ID("VAL015", 400, "The ticket id is required.", TechnicalMessageType.BAD_REQUEST_MSG),

    //MS ERRORS
    ERROR_MS_INTERNAL_SERVER("500", 500, TechnicalMessageType.INTERNAL_SERVER_MSG, TechnicalMessageType.INTERNAL_SERVER_MSG),
    ERROR_MS_BAD_REQUEST("400", 400, TechnicalMessageType.BAD_REQUEST_MSG, TechnicalMessageType.BAD_REQUEST_MSG),
    SUCCESS("200", 200, TechnicalMessageType.SUCCESS_MSG, TechnicalMessageType.SUCCESS_MSG),
    ERROR_MS_RESERVE_TICKET("RT-001", 400, "The reserve cannot be completed", TechnicalMessageType.UNAVAILABLE_TICKETS_MSG),

    //ADAPTER ERRORS
    JSON_PROCESSING_ERROR("JP-001", 500, "Technical error with Json Processing", TechnicalMessageType.INTERNAL_SERVER_MSG),
    ERROR_MS_DYNAMO_ERROR("DE-001", 500, "There was an error with the Dynamo adapter.", TechnicalMessageType.INTERNAL_SERVER_MSG),
    ERROR_EVENT_PUBLISH("SQS-001", 500, "There was an error with the SQS adapter.", TechnicalMessageType.INTERNAL_SERVER_MSG);


    private final String code;
    private final int externalCode;
    private final String message;
    private final String externalMessage;

    private static final String BAD_REQUEST_MSG = "The request could not be processed due to invalid or incomplete data.";
    private static final String INTERNAL_SERVER_MSG = "An unexpected server error occurred. Please try again later.";
    private static final String SUCCESS_MSG = "SUCCESS";
    private static final String NOT_FOUND_MSG = "The provided data was not found.";
    private static final String FORBIDDEN_MSG = "Access to the requested resource is forbidden.";
    private static final String UNAVAILABLE_TICKETS_MSG = "The event cant place more tickets.";
}

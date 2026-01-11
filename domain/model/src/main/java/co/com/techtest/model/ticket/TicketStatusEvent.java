package co.com.techtest.model.ticket;

import co.com.techtest.model.util.enums.ticket.TicketStatus;

public record TicketStatusEvent(TicketStatus currentStatus, Long dateTStamp) {
}

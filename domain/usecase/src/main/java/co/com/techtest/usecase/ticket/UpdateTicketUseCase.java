package co.com.techtest.usecase.ticket;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketStatusEvent;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.model.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static co.com.techtest.model.util.enums.ticket.TicketStatus.isModifiableStatus;
import static co.com.techtest.model.util.enums.ticket.TicketStatus.isNonReversibleReversed;

@RequiredArgsConstructor
public class UpdateTicketUseCase {

    private final TicketGateway ticketGateway;

    public Mono<Ticket> updateTicketStatus(String ticketId, TicketStatus newStatus) {
        return ticketGateway.getTicketById(ticketId)
                .filter(ticket -> isModifiableStatus(ticket.currentStatus()))
                .map(ticket -> updateTicketStatus(ticket, resolveNewStatus(ticket, newStatus)))
                .flatMap(this::handleInventoryUpdate)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessageType.ERROR_MS_NON_MODIFIABLE_TICKET))));
    }

    private Mono<Ticket> handleInventoryUpdate(Ticket ticket) {
        return Mono.just(ticket)
                .filter(ticket1 -> isNonReversibleReversed(ticket1.currentStatus()))
                .flatMap(ticketGateway::updateTicketSold)
                .switchIfEmpty(Mono.defer(() -> this.handleRelease(ticket)));
    }

    private Mono<Ticket> handleRelease(Ticket ticket) {
        return Mono.just(ticket)
                .filter(ticket1 -> TicketStatus.PENDING_CONFIRMATION.equals(ticket1.currentStatus()))
                .flatMap(ticketGateway::updateTicketOnly)
                .switchIfEmpty(Mono.defer(() -> ticketGateway.updateTicketRelease(ticket)));

    }

    private TicketStatus resolveNewStatus(Ticket ticket, TicketStatus newStatus) {
        return ticket.expiresAt().isAfter(LocalDateTime.now()) ? newStatus : TicketStatus.EXPIRED;
    }

    private Ticket updateTicketStatus(Ticket ticket, TicketStatus newStatus) {
        List<TicketStatusEvent> updatedEvents = new ArrayList<>(ticket.ticketStatusEvents());
        updatedEvents.add(new TicketStatusEvent(newStatus, System.currentTimeMillis()));
        return new Ticket(
                ticket.ticketId(),
                ticket.eventId(),
                ticket.userId(),
                ticket.quantity(),
                newStatus,
                updatedEvents,
                ticket.createdAt(),
                ticket.expiresAt()
        );
    }
}

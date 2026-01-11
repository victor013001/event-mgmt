package co.com.techtest.usecase.orchestrator;

import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.usecase.ticket.FindTicketsByStatusUseCase;
import co.com.techtest.usecase.ticket.UpdateTicketUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TicketExpirationOrchestrator {

    private final FindTicketsByStatusUseCase findTicketsByStatusUseCase;
    private final UpdateTicketUseCase updateTicketUseCase;

    public Mono<Void> expireReservedTickets() {
        return findTicketsByStatusUseCase.findByStatus(TicketStatus.RESERVED)
                .filter(ticket -> ticket.expiresAt().isBefore(LocalDateTime.now()))
                .flatMap(ticket -> updateTicketUseCase.updateTicketStatus(ticket.ticketId(), TicketStatus.EXPIRED)
                        .onErrorResume(_ -> Mono.empty()))
                .then();
    }
}

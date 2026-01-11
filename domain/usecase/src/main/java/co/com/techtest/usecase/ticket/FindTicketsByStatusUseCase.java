package co.com.techtest.usecase.ticket;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class FindTicketsByStatusUseCase {

    private final TicketGateway ticketGateway;

    public Flux<Ticket> findByStatus(TicketStatus status) {
        return ticketGateway.findTicketsByStatus(status);
    }
}
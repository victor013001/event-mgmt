package co.com.techtest.model.ticket.gateway;

import co.com.techtest.model.ticket.Ticket;
import reactor.core.publisher.Mono;

public interface TicketGateway {
    Mono<Ticket> saveTicket(Ticket ticket);

    Mono<Ticket> updateTicketSold(Ticket ticket);

    Mono<Ticket> updateTicketRelease(Ticket ticket);

    Mono<Ticket> updateTicketOnly(Ticket ticket);

    Mono<Ticket> getTicketById(String ticketId);
}

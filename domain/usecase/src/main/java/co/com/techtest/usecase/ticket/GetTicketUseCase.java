package co.com.techtest.usecase.ticket;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetTicketUseCase {

    private final TicketGateway ticketGateway;

    public Mono<Ticket> getTicketById(String ticketId, String userId) {
        return ticketGateway.getTicketById(ticketId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessageType.ERROR_TICKET_NOT_FOUND)))
                .filter(ticket -> ticket.userId().equals(userId))
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessageType.ERROR_TICKET_ACCESS_DENIED)));
    }
}
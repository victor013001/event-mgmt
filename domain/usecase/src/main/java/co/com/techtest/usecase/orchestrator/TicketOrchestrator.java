package co.com.techtest.usecase.orchestrator;

import co.com.techtest.model.inventory.gateway.InventoryGateway;
import co.com.techtest.model.purchase.PurchaseParameter;
import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketParameter;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.purchase.PurchaseUseCase;
import co.com.techtest.usecase.ticket.TicketUseCase;
import co.com.techtest.usecase.ticket.UpdateTicketUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TicketOrchestrator {

    private final InventoryGateway inventoryGateway;
    private final TicketUseCase ticketUseCase;
    private final UpdateTicketUseCase updateTicketUseCase;
    private final PurchaseUseCase purchaseUseCase;

    public Mono<Ticket> placeTicket(TicketParameter parameter) {
        return inventoryGateway.getEventInventory(parameter.eventId())
                .flatMap(_ -> ticketUseCase.createTicket(parameter))
                .flatMap(ticket -> purchaseUseCase.executePurchase(
                                new PurchaseParameter(parameter.flowId(), parameter.userId(), ticket.ticketId())
                        )
                        .flatMap(purchaseParameter -> updateTicketUseCase.updateTicketStatus(
                                purchaseParameter.ticketId(), TicketStatus.PENDING_CONFIRMATION
                        )))
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessageType.ERROR_MS_EVENT_NOT_FOUND)));
    }
}

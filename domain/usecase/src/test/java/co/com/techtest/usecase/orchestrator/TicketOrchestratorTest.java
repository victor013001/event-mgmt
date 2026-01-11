package co.com.techtest.usecase.orchestrator;

import co.com.techtest.model.inventory.Inventory;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketOrchestratorTest {

    @Mock
    private InventoryGateway inventoryGateway;
    @Mock
    private TicketUseCase ticketUseCase;
    @Mock
    private UpdateTicketUseCase updateTicketUseCase;
    @Mock
    private PurchaseUseCase purchaseUseCase;

    private TicketOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new TicketOrchestrator(inventoryGateway, ticketUseCase, updateTicketUseCase, purchaseUseCase);
    }

    @Test
    void shouldPlaceTicketSuccessfully() {
        TicketParameter parameter = new TicketParameter("flow123", "user123", "event123", 2);
        Inventory inventory = new Inventory("event123", 100L, 90L, 10L, 0L, 0L);
        Ticket ticket = new Ticket("ticket123", "event123", "user123", 2, TicketStatus.RESERVED,
                new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        Ticket updatedTicket = new Ticket("ticket123", "event123", "user123", 2, TicketStatus.PENDING_CONFIRMATION,
                new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));

        when(inventoryGateway.getEventInventory("event123")).thenReturn(Mono.just(inventory));
        when(ticketUseCase.createTicket(parameter)).thenReturn(Mono.just(ticket));
        when(purchaseUseCase.executePurchase(any(PurchaseParameter.class))).thenReturn(
                Mono.just(new PurchaseParameter("flow123", "user123", "ticket123")));
        when(updateTicketUseCase.updateTicketStatus(eq("ticket123"), eq(TicketStatus.PENDING_CONFIRMATION))).thenReturn(Mono.just(updatedTicket));

        StepVerifier.create(orchestrator.placeTicket(parameter))
                .expectNext(updatedTicket)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        TicketParameter parameter = new TicketParameter("flow123", "user123", "nonexistent", 2);

        when(inventoryGateway.getEventInventory("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(orchestrator.placeTicket(parameter))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessageType.ERROR_MS_EVENT_NOT_FOUND)
                .verify();
    }

    @Test
    void shouldPropagateTicketCreationError() {
        TicketParameter parameter = new TicketParameter("flow123", "user123", "event123", 2);
        Inventory inventory = new Inventory("event123", 100L, 90L, 10L, 0L, 0L);
        RuntimeException ticketError = new RuntimeException("Ticket creation failed");

        when(inventoryGateway.getEventInventory("event123")).thenReturn(Mono.just(inventory));
        when(ticketUseCase.createTicket(parameter)).thenReturn(Mono.error(ticketError));

        StepVerifier.create(orchestrator.placeTicket(parameter))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldPropagatePurchaseError() {
        TicketParameter parameter = new TicketParameter("flow123", "user123", "event123", 2);
        Inventory inventory = new Inventory("event123", 100L, 90L, 10L, 0L, 0L);
        Ticket ticket = new Ticket("ticket123", "event123", "user123", 2, TicketStatus.RESERVED,
                new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        RuntimeException purchaseError = new RuntimeException("Purchase failed");

        when(inventoryGateway.getEventInventory("event123")).thenReturn(Mono.just(inventory));
        when(ticketUseCase.createTicket(parameter)).thenReturn(Mono.just(ticket));
        when(purchaseUseCase.executePurchase(any(PurchaseParameter.class))).thenReturn(Mono.error(purchaseError));

        StepVerifier.create(orchestrator.placeTicket(parameter))
                .expectError(RuntimeException.class)
                .verify();
    }
}

package co.com.techtest.dynamodb.ticket;

import co.com.techtest.dynamodb.inventory.repository.InventoryRepository;
import co.com.techtest.dynamodb.ticket.repository.TicketRepository;
import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketStatusEvent;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.model.util.exception.TechnicalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsResponse;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketDynamoAdapterTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @InjectMocks
    private TicketDynamoAdapter ticketDynamoAdapter;

    private Ticket createTestTicket() {
        return new Ticket(
                "ticket123",
                "event123",
                "user123",
                2,
                TicketStatus.RESERVED,
                List.of(new TicketStatusEvent(TicketStatus.RESERVED, System.currentTimeMillis())),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );
    }

    @Test
    void shouldSaveTicket() {
        Ticket ticket = createTestTicket();
        TransactWriteItem ticketTransaction = TransactWriteItem.builder().build();
        TransactWriteItem inventoryTransaction = TransactWriteItem.builder().build();

        when(ticketRepository.createTicketTransaction(ticket)).thenReturn(Mono.just(ticketTransaction));
        when(inventoryRepository.createInventoryReservationTransaction(ticket.eventId(), ticket.quantity()))
                .thenReturn(Mono.just(inventoryTransaction));
        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(TransactWriteItemsResponse.builder().build()));

        StepVerifier.create(ticketDynamoAdapter.saveTicket(ticket))
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void shouldHandleTransactionCanceledExceptionOnSave() {
        Ticket ticket = createTestTicket();
        TransactWriteItem ticketTransaction = TransactWriteItem.builder().build();
        TransactWriteItem inventoryTransaction = TransactWriteItem.builder().build();

        when(ticketRepository.createTicketTransaction(ticket)).thenReturn(Mono.just(ticketTransaction));
        when(inventoryRepository.createInventoryReservationTransaction(ticket.eventId(), ticket.quantity()))
                .thenReturn(Mono.just(inventoryTransaction));
        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(TransactionCanceledException.builder().build()));

        StepVerifier.create(ticketDynamoAdapter.saveTicket(ticket))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void shouldHandleDynamoDbExceptionOnSave() {
        Ticket ticket = createTestTicket();
        TransactWriteItem ticketTransaction = TransactWriteItem.builder().build();
        TransactWriteItem inventoryTransaction = TransactWriteItem.builder().build();

        when(ticketRepository.createTicketTransaction(ticket)).thenReturn(Mono.just(ticketTransaction));
        when(inventoryRepository.createInventoryReservationTransaction(ticket.eventId(), ticket.quantity()))
                .thenReturn(Mono.just(inventoryTransaction));
        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(DynamoDbException.builder().build()));

        StepVerifier.create(ticketDynamoAdapter.saveTicket(ticket))
                .expectError(TechnicalException.class)
                .verify();
    }

    @Test
    void shouldGetTicketById() {
        String ticketId = "ticket123";
        Ticket ticket = createTestTicket();

        when(ticketRepository.getById(ticketId)).thenReturn(Mono.just(ticket));

        StepVerifier.create(ticketDynamoAdapter.getTicketById(ticketId))
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void shouldHandleTicketNotFound() {
        String ticketId = "nonexistent";

        when(ticketRepository.getById(ticketId)).thenReturn(Mono.empty());

        StepVerifier.create(ticketDynamoAdapter.getTicketById(ticketId))
                .expectError(TechnicalException.class)
                .verify();
    }

    @Test
    void shouldHandleDynamoDbExceptionOnGet() {
        String ticketId = "ticket123";

        when(ticketRepository.getById(ticketId))
                .thenReturn(Mono.error(DynamoDbException.builder().build()));

        StepVerifier.create(ticketDynamoAdapter.getTicketById(ticketId))
                .expectError(TechnicalException.class)
                .verify();
    }

    @Test
    void shouldHandleDynamoDbExceptionOnUpdateTicketSold() {
        Ticket ticket = createTestTicket();
        TransactWriteItem ticketTransaction = TransactWriteItem.builder().build();
        TransactWriteItem inventoryTransaction = TransactWriteItem.builder().build();

        when(ticketRepository.createTicketUpdateTransaction(ticket)).thenReturn(Mono.just(ticketTransaction));
        when(inventoryRepository.createInventorySoldTransaction(ticket.eventId(), ticket.quantity()))
                .thenReturn(Mono.just(inventoryTransaction));
        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(DynamoDbException.builder().build()));

        StepVerifier.create(ticketDynamoAdapter.updateTicketSold(ticket))
                .expectError(TechnicalException.class)
                .verify();
    }

    @Test
    void shouldUpdateTicketSold() {
        Ticket ticket = createTestTicket();
        TransactWriteItem ticketTransaction = TransactWriteItem.builder().build();
        TransactWriteItem inventoryTransaction = TransactWriteItem.builder().build();

        when(ticketRepository.createTicketUpdateTransaction(ticket)).thenReturn(Mono.just(ticketTransaction));
        when(inventoryRepository.createInventorySoldTransaction(ticket.eventId(), ticket.quantity()))
                .thenReturn(Mono.just(inventoryTransaction));
        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(TransactWriteItemsResponse.builder().build()));

        StepVerifier.create(ticketDynamoAdapter.updateTicketSold(ticket))
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void shouldUpdateTicketRelease() {
        Ticket ticket = createTestTicket();
        TransactWriteItem ticketTransaction = TransactWriteItem.builder().build();
        TransactWriteItem inventoryTransaction = TransactWriteItem.builder().build();

        when(ticketRepository.createTicketUpdateTransaction(ticket)).thenReturn(Mono.just(ticketTransaction));
        when(inventoryRepository.createInventoryReleaseTransaction(ticket.eventId(), ticket.quantity()))
                .thenReturn(Mono.just(inventoryTransaction));
        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(TransactWriteItemsResponse.builder().build()));

        StepVerifier.create(ticketDynamoAdapter.updateTicketRelease(ticket))
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void shouldHandleDynamoDbExceptionOnUpdateTicketRelease() {
        Ticket ticket = createTestTicket();
        TransactWriteItem ticketTransaction = TransactWriteItem.builder().build();
        TransactWriteItem inventoryTransaction = TransactWriteItem.builder().build();

        when(ticketRepository.createTicketUpdateTransaction(ticket)).thenReturn(Mono.just(ticketTransaction));
        when(inventoryRepository.createInventoryReleaseTransaction(ticket.eventId(), ticket.quantity()))
                .thenReturn(Mono.just(inventoryTransaction));
        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(DynamoDbException.builder().build()));

        StepVerifier.create(ticketDynamoAdapter.updateTicketRelease(ticket))
                .expectError(TechnicalException.class)
                .verify();
    }

    @Test
    void shouldUpdateTicketOnly() {
        Ticket ticket = createTestTicket();

        when(ticketRepository.save(ticket)).thenReturn(Mono.just(ticket));

        StepVerifier.create(ticketDynamoAdapter.updateTicketOnly(ticket))
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void shouldFindTicketsByStatus() {
        Ticket ticket = createTestTicket();

        when(ticketRepository.findByStatus(TicketStatus.RESERVED))
                .thenReturn(Flux.just(ticket));

        StepVerifier.create(ticketDynamoAdapter.findTicketsByStatus(TicketStatus.RESERVED))
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void shouldHandleDynamoDbExceptionOnFindTicketsByStatus() {
        when(ticketRepository.findByStatus(TicketStatus.RESERVED))
                .thenReturn(Flux.error(DynamoDbException.builder().build()));

        StepVerifier.create(ticketDynamoAdapter.findTicketsByStatus(TicketStatus.RESERVED))
                .expectError(TechnicalException.class)
                .verify();
    }
}

package co.com.techtest.dynamodb.ticket;

import co.com.techtest.dynamodb.inventory.repository.InventoryRepository;
import co.com.techtest.dynamodb.ticket.repository.TicketRepository;
import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.model.util.exception.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketDynamoAdapter implements TicketGateway {

    private final TicketRepository ticketRepository;
    private final InventoryRepository inventoryRepository;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    private static final String SAVE_TICKET_REQUEST = "Save Ticket Dynamo Adapter";
    private static final String SAVE_TICKET_KEY_REQUEST = "saveTicketDynamoAdapterRQ";
    private static final String SAVE_TICKET_RESPONSE = "Save Ticket Dynamo Adapter Response";
    private static final String SAVE_TICKET_KEY_RESPONSE = "saveTicketDynamoAdapterRS";
    private static final String SAVE_TICKET_ERROR_RESPONSE = "Save Ticket Dynamo Adapter Error Response";
    private static final String SAVE_TICKET_KEY_ERROR_RESPONSE = "saveTicketDynamoAdapterErrorRS";

    private static final String GET_TICKET_REQUEST = "Get Ticket Dynamo Adapter";
    private static final String GET_TICKET_KEY_REQUEST = "getTicketDynamoAdapterRQ";
    private static final String GET_TICKET_RESPONSE = "Get Ticket Dynamo Adapter Response";
    private static final String GET_TICKET_KEY_RESPONSE = "getTicketDynamoAdapterRS";
    private static final String GET_TICKET_ERROR_RESPONSE = "Get Ticket Dynamo Adapter Error Response";
    private static final String GET_TICKET_KEY_ERROR_RESPONSE = "getTicketDynamoAdapterErrorRS";

    private static final String UPDATE_TICKET_SOLD_REQUEST = "Update Ticket Status Sold Dynamo Adapter";
    private static final String UPDATE_TICKET_SOLD_KEY_REQUEST = "updateTicketStatusSoldDynamoAdapterRQ";
    private static final String UPDATE_TICKET_SOLD_RESPONSE = "Update Ticket Status Sold Dynamo Adapter Response";
    private static final String UPDATE_TICKET_SOLD_KEY_RESPONSE = "updateTicketStatusSoldDynamoAdapterRS";
    private static final String UPDATE_TICKET_SOLD_ERROR_RESPONSE = "Update Ticket Status Sold Dynamo Adapter Error Response";
    private static final String UPDATE_TICKET_SOLD_KEY_ERROR_RESPONSE = "updateTicketStatusSoldDynamoAdapterErrorRS";

    private static final String UPDATE_TICKET_RELEASE_REQUEST = "Update Ticket Status Release Dynamo Adapter";
    private static final String UPDATE_TICKET_RELEASE_KEY_REQUEST = "updateTicketStatusReleaseDynamoAdapterRQ";
    private static final String UPDATE_TICKET_RELEASE_RESPONSE = "Update Ticket Status Release Dynamo Adapter Response";
    private static final String UPDATE_TICKET_RELEASE_KEY_RESPONSE = "updateTicketStatusReleaseDynamoAdapterRS";
    private static final String UPDATE_TICKET_RELEASE_ERROR_RESPONSE = "Update Ticket Status Release Dynamo Adapter Error Response";
    private static final String UPDATE_TICKET_RELEASE_KEY_ERROR_RESPONSE = "updateTicketStatusReleaseDynamoAdapterErrorRS";

    private static final String UPDATE_TICKET_ONLY_REQUEST = "Update Ticket Only Dynamo Adapter";
    private static final String UPDATE_TICKET_ONLY_KEY_REQUEST = "updateTicketOnlyDynamoAdapterRQ";
    private static final String UPDATE_TICKET_ONLY_RESPONSE = "Update Ticket Only Dynamo Adapter Response";
    private static final String UPDATE_TICKET_ONLY_KEY_RESPONSE = "updateTicketOnlyDynamoAdapterRS";
    private static final String UPDATE_TICKET_ONLY_ERROR_RESPONSE = "Update Ticket Only Dynamo Adapter Error Response";
    private static final String UPDATE_TICKET_ONLY_KEY_ERROR_RESPONSE = "updateTicketOnlyDynamoAdapterErrorRS";

    @Override
    public Mono<Ticket> saveTicket(Ticket ticket) {
        return Mono.zip(
                        ticketRepository.createTicketTransaction(ticket),
                        inventoryRepository.createInventoryReservationTransaction(ticket.eventId(), ticket.quantity())
                )
                .flatMap(transactions -> executeTransaction(List.of(transactions.getT1(), transactions.getT2())))
                .thenReturn(ticket)
                .doOnSubscribe(_ -> log.info(SAVE_TICKET_REQUEST, kv(SAVE_TICKET_KEY_REQUEST, ticket)))
                .doOnNext(saved -> log.info(SAVE_TICKET_RESPONSE, kv(SAVE_TICKET_KEY_RESPONSE, saved)))
                .doOnError(error -> log.error(SAVE_TICKET_ERROR_RESPONSE, kv(SAVE_TICKET_KEY_ERROR_RESPONSE, error)))
                .onErrorMap(TransactionCanceledException.class, _ -> new BusinessException(TechnicalMessageType.ERROR_MS_RESERVE_TICKET))
                .onErrorMap(DynamoDbException.class, exception -> new TechnicalException(exception, TechnicalMessageType.ERROR_MS_DYNAMO_ERROR));
    }

    @Override
    public Mono<Ticket> getTicketById(String ticketId) {
        return ticketRepository.getById(ticketId)
                .switchIfEmpty(Mono.error(new TechnicalException(null, TechnicalMessageType.ERROR_MS_EVENT_NOT_FOUND)))
                .doOnSubscribe(_ -> log.info(GET_TICKET_REQUEST, kv(GET_TICKET_KEY_REQUEST, ticketId)))
                .doOnNext(ticket -> log.info(GET_TICKET_RESPONSE, kv(GET_TICKET_KEY_RESPONSE, ticket)))
                .doOnError(error -> log.error(GET_TICKET_ERROR_RESPONSE, kv(GET_TICKET_KEY_ERROR_RESPONSE, error)))
                .onErrorMap(DynamoDbException.class, exception -> new TechnicalException(exception, TechnicalMessageType.ERROR_MS_DYNAMO_ERROR));
    }

    @Override
    public Mono<Ticket> updateTicketSold(Ticket ticket) {
        Mono<TransactWriteItem> ticketTransaction = ticketRepository.createTicketUpdateTransaction(ticket);
        Mono<TransactWriteItem> inventoryTransaction = inventoryRepository.createInventorySoldTransaction(ticket.eventId(), ticket.quantity());

        return Mono.zip(ticketTransaction, inventoryTransaction)
                .flatMap(transactions -> executeTransaction(List.of(transactions.getT1(), transactions.getT2())))
                .thenReturn(ticket)
                .doOnSubscribe(_ -> log.info(UPDATE_TICKET_SOLD_REQUEST, kv(UPDATE_TICKET_SOLD_KEY_REQUEST, ticket)))
                .doOnNext(updated -> log.info(UPDATE_TICKET_SOLD_RESPONSE, kv(UPDATE_TICKET_SOLD_KEY_RESPONSE, updated)))
                .doOnError(error -> log.error(UPDATE_TICKET_SOLD_ERROR_RESPONSE, kv(UPDATE_TICKET_SOLD_KEY_ERROR_RESPONSE, error)))
                .onErrorMap(DynamoDbException.class, exception -> new TechnicalException(exception, TechnicalMessageType.ERROR_MS_DYNAMO_ERROR));
    }

    @Override
    public Mono<Ticket> updateTicketRelease(Ticket ticket) {
        Mono<TransactWriteItem> ticketTransaction = ticketRepository.createTicketUpdateTransaction(ticket);
        Mono<TransactWriteItem> inventoryTransaction = inventoryRepository.createInventoryReleaseTransaction(ticket.eventId(), ticket.quantity());

        return Mono.zip(ticketTransaction, inventoryTransaction)
                .flatMap(transactions -> executeTransaction(List.of(transactions.getT1(), transactions.getT2())))
                .thenReturn(ticket)
                .doOnSubscribe(_ -> log.info(UPDATE_TICKET_RELEASE_REQUEST, kv(UPDATE_TICKET_RELEASE_KEY_REQUEST, ticket)))
                .doOnNext(updated -> log.info(UPDATE_TICKET_RELEASE_RESPONSE, kv(UPDATE_TICKET_RELEASE_KEY_RESPONSE, updated)))
                .doOnError(error -> log.error(UPDATE_TICKET_RELEASE_ERROR_RESPONSE, kv(UPDATE_TICKET_RELEASE_KEY_ERROR_RESPONSE, error)))
                .onErrorMap(DynamoDbException.class, exception -> new TechnicalException(exception, TechnicalMessageType.ERROR_MS_DYNAMO_ERROR));
    }

    @Override
    public Mono<Ticket> updateTicketOnly(Ticket ticket) {
        return ticketRepository.save(ticket)
                .doOnSubscribe(_ -> log.info(UPDATE_TICKET_ONLY_REQUEST, kv(UPDATE_TICKET_ONLY_KEY_REQUEST, ticket)))
                .doOnNext(updated -> log.info(UPDATE_TICKET_ONLY_RESPONSE, kv(UPDATE_TICKET_ONLY_KEY_RESPONSE, updated)))
                .doOnError(error -> log.error(UPDATE_TICKET_ONLY_ERROR_RESPONSE, kv(UPDATE_TICKET_ONLY_KEY_ERROR_RESPONSE, error)))
                .onErrorMap(DynamoDbException.class, exception -> new TechnicalException(exception, TechnicalMessageType.ERROR_MS_DYNAMO_ERROR));
    }

    private Mono<Void> executeTransaction(List<TransactWriteItem> transactItems) {
        TransactWriteItemsRequest request = TransactWriteItemsRequest.builder()
                .transactItems(transactItems)
                .build();

        return Mono.fromFuture(dynamoDbAsyncClient.transactWriteItems(request))
                .then();
    }
}

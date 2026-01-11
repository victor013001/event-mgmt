package co.com.techtest.dynamodb.ticket.repository;

import co.com.techtest.dynamodb.repository.GenericOperationsRepository;
import co.com.techtest.dynamodb.ticket.mapper.TicketDataMapper;
import co.com.techtest.dynamodb.ticket.model.TicketData;
import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;

import java.util.Map;

@Repository
public class TicketRepository extends GenericOperationsRepository<Ticket, String, TicketData> {

    private static final String STATUS_INDEX = "status-index";
    private static final String TICKET_ID_FIELD = "ticketId";
    private static final String EVENT_ID_FIELD = "eventId";
    private static final String USER_ID_FIELD = "userId";
    private static final String QUANTITY_FIELD = "quantity";
    private static final String STATUS_FIELD = "status";
    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String EXPIRES_AT_FIELD = "expiresAt";
    private static final String CONDITION_NOT_EXISTS = "attribute_not_exists(ticketId)";

    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    private final TicketDataMapper ticketDataMapper;
    private final String tableName;

    public TicketRepository(@Qualifier("getDynamoDbEnhancedAsyncClient") DynamoDbEnhancedAsyncClient client,
                            TicketDataMapper ticketDataMapper,
                            @Value("aws.region.dynamodb.tickets.tableName") String tableName) {
        super(client, ticketDataMapper::toDomain, ticketDataMapper::toData, tableName, STATUS_INDEX);
        this.dynamoDbEnhancedAsyncClient = client;
        this.ticketDataMapper = ticketDataMapper;
        this.tableName = tableName;
    }

    public Mono<TransactWriteItem> createTicketTransaction(Ticket ticket) {
        TicketData ticketData = ticketDataMapper.toData(ticket);

        Put put = Put.builder()
                .tableName(tableName)
                .item(Map.of(
                        TICKET_ID_FIELD, AttributeValue.builder().s(ticketData.getTicketId()).build(),
                        EVENT_ID_FIELD, AttributeValue.builder().s(ticketData.getEventId()).build(),
                        USER_ID_FIELD, AttributeValue.builder().s(ticketData.getUserId()).build(),
                        QUANTITY_FIELD, AttributeValue.builder().n(ticketData.getQuantity().toString()).build(),
                        STATUS_FIELD, AttributeValue.builder().s(ticketData.getStatus()).build(),
                        CREATED_AT_FIELD, AttributeValue.builder().n(ticketData.getCreatedAt().toString()).build(),
                        EXPIRES_AT_FIELD, AttributeValue.builder().n(ticketData.getExpiresAt().toString()).build()
                ))
                .conditionExpression(CONDITION_NOT_EXISTS)
                .build();

        return Mono.just(TransactWriteItem.builder().put(put).build());
    }

    public Mono<TransactWriteItem> createTicketUpdateTransaction(Ticket ticket) {
        TicketData ticketData = ticketDataMapper.toData(ticket);

        Put put = Put.builder()
                .tableName(tableName)
                .item(Map.of(
                        TICKET_ID_FIELD, AttributeValue.builder().s(ticketData.getTicketId()).build(),
                        EVENT_ID_FIELD, AttributeValue.builder().s(ticketData.getEventId()).build(),
                        USER_ID_FIELD, AttributeValue.builder().s(ticketData.getUserId()).build(),
                        QUANTITY_FIELD, AttributeValue.builder().n(ticketData.getQuantity().toString()).build(),
                        STATUS_FIELD, AttributeValue.builder().s(ticketData.getStatus()).build(),
                        CREATED_AT_FIELD, AttributeValue.builder().n(ticketData.getCreatedAt().toString()).build(),
                        EXPIRES_AT_FIELD, AttributeValue.builder().n(ticketData.getExpiresAt().toString()).build()
                ))
                .build();

        return Mono.just(TransactWriteItem.builder().put(put).build());
    }

    public Flux<Ticket> findByStatus(TicketStatus status) {
        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(status.name())
                        .build()))
                .build();
        return queryFluxByIndex(query, STATUS_INDEX);
    }
}

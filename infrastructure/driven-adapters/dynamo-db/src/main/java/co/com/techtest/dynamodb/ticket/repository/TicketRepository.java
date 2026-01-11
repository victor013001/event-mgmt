package co.com.techtest.dynamodb.ticket.repository;

import co.com.techtest.dynamodb.repository.GenericOperationsRepository;
import co.com.techtest.dynamodb.ticket.mapper.TicketDataMapper;
import co.com.techtest.dynamodb.ticket.model.TicketData;
import co.com.techtest.model.ticket.Ticket;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;

import java.util.Map;

@Repository
public class TicketRepository extends GenericOperationsRepository<Ticket, String, TicketData> {

    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    private final TicketDataMapper ticketDataMapper;
    private final String tableName;

    public TicketRepository(@Qualifier("getDynamoDbEnhancedAsyncClient") DynamoDbEnhancedAsyncClient client,
                            TicketDataMapper ticketDataMapper,
                            @Value("aws.region.dynamodb.tickets.tableName") String tableName) {
        super(client, ticketDataMapper::toDomain, ticketDataMapper::toData, tableName);
        this.dynamoDbEnhancedAsyncClient = client;
        this.ticketDataMapper = ticketDataMapper;
        this.tableName = tableName;
    }

    public Mono<TransactWriteItem> createTicketTransaction(Ticket ticket) {
        TicketData ticketData = ticketDataMapper.toData(ticket);

        Put put = Put.builder()
                .tableName(tableName)
                .item(Map.of(
                        "ticketId", AttributeValue.builder().s(ticketData.getTicketId()).build(),
                        "eventId", AttributeValue.builder().s(ticketData.getEventId()).build(),
                        "userId", AttributeValue.builder().s(ticketData.getUserId()).build(),
                        "quantity", AttributeValue.builder().n(ticketData.getQuantity().toString()).build(),
                        "status", AttributeValue.builder().s(ticketData.getStatus()).build(),
                        "createdAt", AttributeValue.builder().n(ticketData.getCreatedAt().toString()).build(),
                        "expiresAt", AttributeValue.builder().n(ticketData.getExpiresAt().toString()).build()
                ))
                .conditionExpression("attribute_not_exists(ticketId)")
                .build();

        return Mono.just(TransactWriteItem.builder().put(put).build());
    }

    public Mono<TransactWriteItem> createTicketUpdateTransaction(Ticket ticket) {
        TicketData ticketData = ticketDataMapper.toData(ticket);

        Put put = Put.builder()
                .tableName(tableName)
                .item(Map.of(
                        "ticketId", AttributeValue.builder().s(ticketData.getTicketId()).build(),
                        "eventId", AttributeValue.builder().s(ticketData.getEventId()).build(),
                        "userId", AttributeValue.builder().s(ticketData.getUserId()).build(),
                        "quantity", AttributeValue.builder().n(ticketData.getQuantity().toString()).build(),
                        "status", AttributeValue.builder().s(ticketData.getStatus()).build(),
                        "createdAt", AttributeValue.builder().n(ticketData.getCreatedAt().toString()).build(),
                        "expiresAt", AttributeValue.builder().n(ticketData.getExpiresAt().toString()).build()
                ))
                .build();

        return Mono.just(TransactWriteItem.builder().put(put).build());
    }
}

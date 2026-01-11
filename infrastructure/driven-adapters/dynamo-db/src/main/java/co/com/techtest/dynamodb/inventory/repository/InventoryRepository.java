package co.com.techtest.dynamodb.inventory.repository;

import co.com.techtest.dynamodb.inventory.mapper.InventoryDataMapper;
import co.com.techtest.dynamodb.inventory.model.InventoryData;
import co.com.techtest.dynamodb.repository.GenericOperationsRepository;
import co.com.techtest.model.inventory.Inventory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.Update;

import java.util.Map;

@Repository
public class InventoryRepository extends GenericOperationsRepository<Inventory, String, InventoryData> {

    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final InventoryDataMapper inventoryDataMapper;
    private final String tableName;

    public InventoryRepository(@Qualifier("getDynamoDbEnhancedAsyncClient") DynamoDbEnhancedAsyncClient client,
                               DynamoDbAsyncClient dynamoDbAsyncClient,
                               InventoryDataMapper inventoryDataMapper,
                               @Value("aws.region.dynamodb.inventory.tableName") String tableName) {
        super(client, inventoryDataMapper::toDomain, inventoryDataMapper::toData, tableName);
        this.dynamoDbEnhancedAsyncClient = client;
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
        this.inventoryDataMapper = inventoryDataMapper;
        this.tableName = tableName;
    }

    public Mono<TransactWriteItem> createInventoryReservationTransaction(String eventId, Integer quantity) {
        Update update = Update.builder()
                .tableName(tableName)
                .key(Map.of("eventId", AttributeValue.builder().s(eventId).build()))
                .updateExpression("SET available = available - :qty, reserved = reserved + :qty")
                .conditionExpression("available >= :qty")
                .expressionAttributeValues(Map.of(
                        ":qty", AttributeValue.builder().n(quantity.toString()).build()
                ))
                .build();

        return Mono.just(TransactWriteItem.builder().update(update).build());
    }

    public Mono<TransactWriteItem> createInventoryReleaseTransaction(String eventId, Integer quantity) {
        Update update = Update.builder()
                .tableName(tableName)
                .key(Map.of("eventId", AttributeValue.builder().s(eventId).build()))
                .updateExpression("SET available = available + :qty, reserved = reserved - :qty")
                .conditionExpression("reserved >= :qty")
                .expressionAttributeValues(Map.of(
                        ":qty", AttributeValue.builder().n(quantity.toString()).build()
                ))
                .build();

        return Mono.just(TransactWriteItem.builder().update(update).build());
    }

    public Mono<TransactWriteItem> createInventorySoldTransaction(String eventId, Integer quantity) {
        Update update = Update.builder()
                .tableName(tableName)
                .key(Map.of("eventId", AttributeValue.builder().s(eventId).build()))
                .updateExpression("SET reserved = reserved - :qty, sold = sold + :qty")
                .conditionExpression("reserved >= :qty")
                .expressionAttributeValues(Map.of(
                        ":qty", AttributeValue.builder().n(quantity.toString()).build()
                ))
                .build();

        return Mono.just(TransactWriteItem.builder().update(update).build());
    }
}

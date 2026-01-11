package co.com.techtest.dynamodb.inventory.repository;

import co.com.techtest.dynamodb.inventory.mapper.InventoryDataMapper;
import co.com.techtest.dynamodb.inventory.model.InventoryData;
import co.com.techtest.dynamodb.repository.GenericOperationsRepository;
import co.com.techtest.model.inventory.Inventory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class InventoryRepository extends GenericOperationsRepository<Inventory, String, InventoryData> {

    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    private final InventoryDataMapper inventoryDataMapper;
    private final String tableName;

    public InventoryRepository(@Qualifier("getDynamoDbEnhancedAsyncClient") DynamoDbEnhancedAsyncClient client,
                               InventoryDataMapper inventoryDataMapper,
                               @Value("aws.region.dynamodb.inventory.tableName") String tableName) {
        super(client, inventoryDataMapper::toDomain, inventoryDataMapper::toData, tableName);
        this.dynamoDbEnhancedAsyncClient = client;
        this.inventoryDataMapper = inventoryDataMapper;
        this.tableName = tableName;
    }
}

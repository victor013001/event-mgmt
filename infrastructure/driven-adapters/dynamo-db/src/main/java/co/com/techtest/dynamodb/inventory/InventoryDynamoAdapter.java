package co.com.techtest.dynamodb.inventory;

import co.com.techtest.dynamodb.inventory.repository.InventoryRepository;
import co.com.techtest.model.inventory.Inventory;
import co.com.techtest.model.inventory.gateway.InventoryGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryDynamoAdapter implements InventoryGateway {

    private final InventoryRepository inventoryRepository;

    private static final String SAVE_INVENTORY_REQUEST = "Save Inventory Dynamo Adapter";
    private static final String SAVE_INVENTORY_KEY_REQUEST = "saveInventoryDynamoAdapterRQ";
    private static final String SAVE_INVENTORY_RESPONSE = "Save Inventory Dynamo Adapter Response";
    private static final String SAVE_INVENTORY_KEY_RESPONSE = "saveInventoryDynamoAdapterRS";
    private static final String SAVE_INVENTORY_ERROR_RESPONSE = "Save Inventory Dynamo Adapter Error Response";
    private static final String SAVE_INVENTORY_KEY_ERROR_RESPONSE = "saveInventoryDynamoAdapterErrorRS";

    @Override
    public Mono<Inventory> saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory)
                .doOnSubscribe(_ -> log.info(SAVE_INVENTORY_REQUEST, kv(SAVE_INVENTORY_KEY_REQUEST, inventory)))
                .doOnNext(saved -> log.info(SAVE_INVENTORY_RESPONSE, kv(SAVE_INVENTORY_KEY_RESPONSE, saved)))
                .doOnError(error -> log.error(SAVE_INVENTORY_ERROR_RESPONSE, kv(SAVE_INVENTORY_KEY_ERROR_RESPONSE, error)))
                .onErrorMap(DynamoDbException.class, exception -> new TechnicalException(exception, TechnicalMessageType.ERROR_MS_DYNAMO_ERROR));
    }
}

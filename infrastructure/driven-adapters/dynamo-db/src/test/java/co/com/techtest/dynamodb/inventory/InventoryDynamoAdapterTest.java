package co.com.techtest.dynamodb.inventory;

import co.com.techtest.dynamodb.inventory.repository.InventoryRepository;
import co.com.techtest.model.inventory.Inventory;
import co.com.techtest.model.util.exception.TechnicalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryDynamoAdapterTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryDynamoAdapter inventoryDynamoAdapter;

    @Test
    void shouldSaveInventory() {
        Inventory inventory = new Inventory("event1", 100L, 100L, 0L, 0L, 0L);

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(Mono.just(inventory));

        StepVerifier.create(inventoryDynamoAdapter.saveInventory(inventory))
                .expectNext(inventory)
                .verifyComplete();
    }

    @Test
    void shouldGetEventInventory() {
        String eventId = "event1";
        Inventory inventory = new Inventory(eventId, 100L, 90L, 10L, 0L, 0L);

        when(inventoryRepository.getById(eventId)).thenReturn(Mono.just(inventory));

        StepVerifier.create(inventoryDynamoAdapter.getEventInventory(eventId))
                .expectNext(inventory)
                .verifyComplete();
    }

    @Test
    void shouldHandleDynamoDbExceptionOnSave() {
        Inventory inventory = new Inventory("event1", 100L, 100L, 0L, 0L, 0L);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(Mono.error(DynamoDbException.builder().message("DynamoDB error").build()));

        StepVerifier.create(inventoryDynamoAdapter.saveInventory(inventory))
                .expectError(TechnicalException.class)
                .verify();
    }

    @Test
    void shouldHandleDynamoDbExceptionOnGet() {
        String eventId = "event1";

        when(inventoryRepository.getById(eventId))
                .thenReturn(Mono.error(DynamoDbException.builder().message("DynamoDB error").build()));

        StepVerifier.create(inventoryDynamoAdapter.getEventInventory(eventId))
                .expectError(TechnicalException.class)
                .verify();
    }
}

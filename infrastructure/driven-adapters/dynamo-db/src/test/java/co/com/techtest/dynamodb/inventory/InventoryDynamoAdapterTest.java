package co.com.techtest.dynamodb.inventory;

import co.com.techtest.dynamodb.inventory.repository.InventoryRepository;
import co.com.techtest.model.inventory.Inventory;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryDynamoAdapterTest {

    @Mock
    private InventoryRepository inventoryRepository;

    private InventoryDynamoAdapter inventoryDynamoAdapter;

    @BeforeEach
    void setUp() {
        inventoryDynamoAdapter = new InventoryDynamoAdapter(inventoryRepository);
    }

    @Test
    void shouldSaveInventorySuccessfully() {
        Inventory inventory = new Inventory("event-123", 100L, 100L, 0L, 0L);

        given(inventoryRepository.save(inventory)).willReturn(Mono.just(inventory));

        Mono<Inventory> result = inventoryDynamoAdapter.saveInventory(inventory);

        StepVerifier.create(result)
                .expectNext(inventory)
                .verifyComplete();

        verify(inventoryRepository).save(inventory);
    }

    @Test
    void shouldHandleDynamoDbExceptionInSaveInventory() {
        Inventory inventory = new Inventory("event-123", 100L, 100L, 0L, 0L);

        given(inventoryRepository.save(inventory))
                .willReturn(Mono.error(DynamoDbException.builder().message("DynamoDB error").build()));

        Mono<Inventory> result = inventoryDynamoAdapter.saveInventory(inventory);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof TechnicalException &&
                        ((TechnicalException) throwable).getTechnicalMessage() == TechnicalMessageType.ERROR_MS_DYNAMO_ERROR)
                .verify();
    }
}

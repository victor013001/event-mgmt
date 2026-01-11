package co.com.techtest.usecase.inventory;

import co.com.techtest.model.inventory.Inventory;
import co.com.techtest.model.inventory.InventoryParameter;
import co.com.techtest.model.inventory.gateway.InventoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryUseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    private InventoryUseCase inventoryUseCase;

    @BeforeEach
    void setUp() {
        inventoryUseCase = new InventoryUseCase(inventoryGateway);
    }

    @Test
    void shouldCreateEventInventorySuccessfully() {
        InventoryParameter parameter = new InventoryParameter("event-123", 100L);
        Inventory expectedInventory = new Inventory("event-123", 100L, 100L, 0L, 0L);

        given(inventoryGateway.saveInventory(any(Inventory.class)))
                .willReturn(Mono.just(expectedInventory));

        Mono<Inventory> result = inventoryUseCase.creteEventInventory(parameter);

        StepVerifier.create(result)
                .expectNextMatches(inventory -> {
                    assertEquals("event-123", inventory.eventId());
                    assertEquals(100L, inventory.capacity());
                    assertEquals(100L, inventory.available());
                    assertEquals(0L, inventory.reserved());
                    assertEquals(0L, inventory.sold());
                    return true;
                })
                .verifyComplete();

        verify(inventoryGateway).saveInventory(any(Inventory.class));
    }

    @Test
    void shouldPropagateGatewayError() {
        InventoryParameter parameter = new InventoryParameter("event-123", 100L);
        RuntimeException error = new RuntimeException("Gateway error");

        given(inventoryGateway.saveInventory(any(Inventory.class)))
                .willReturn(Mono.error(error));

        Mono<Inventory> result = inventoryUseCase.creteEventInventory(parameter);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}

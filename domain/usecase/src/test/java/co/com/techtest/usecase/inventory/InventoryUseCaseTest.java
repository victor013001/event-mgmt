package co.com.techtest.usecase.inventory;

import co.com.techtest.model.inventory.Inventory;
import co.com.techtest.model.inventory.InventoryParameter;
import co.com.techtest.model.inventory.gateway.InventoryGateway;
import co.com.techtest.model.util.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryUseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private InventoryUseCase inventoryUseCase;

    @Test
    void shouldCreateEventInventory() {
        InventoryParameter parameter = new InventoryParameter("event1", 100L);
        Inventory expectedInventory = new Inventory("event1", 100L, 100L, 0L, 0L, 0L);

        when(inventoryGateway.saveInventory(any(Inventory.class))).thenReturn(Mono.just(expectedInventory));

        StepVerifier.create(inventoryUseCase.creteEventInventory(parameter))
                .expectNext(expectedInventory)
                .verifyComplete();
    }

    @Test
    void shouldGetEventInventory() {
        String eventId = "event1";
        Inventory expectedInventory = new Inventory(eventId, 100L, 90L, 10L, 0L, 0L);

        when(inventoryGateway.getEventInventory(eventId)).thenReturn(Mono.just(expectedInventory));

        StepVerifier.create(inventoryUseCase.getEventInventory(eventId))
                .expectNext(expectedInventory)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        String eventId = "nonexistent";

        when(inventoryGateway.getEventInventory(eventId)).thenReturn(Mono.empty());

        StepVerifier.create(inventoryUseCase.getEventInventory(eventId))
                .expectError(BusinessException.class)
                .verify();
    }
}

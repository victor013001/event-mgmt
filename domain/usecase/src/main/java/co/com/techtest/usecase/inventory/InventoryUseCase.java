package co.com.techtest.usecase.inventory;

import co.com.techtest.model.inventory.Inventory;
import co.com.techtest.model.inventory.InventoryParameter;
import co.com.techtest.model.inventory.gateway.InventoryGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class InventoryUseCase {

    private final InventoryGateway inventoryGateway;

    public Mono<Inventory> creteEventInventory(InventoryParameter inventoryParameter) {
        return Mono.just(inventoryParameter)
                .map(this::buildInventory)
                .flatMap(inventoryGateway::saveInventory);
    }

    public Mono<Inventory> getEventInventory(String eventId) {
        return inventoryGateway.getEventInventory(eventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessageType.ERROR_MS_EVENT_NOT_FOUND))));
    }

    private Inventory buildInventory(InventoryParameter inventoryParameter) {
        return new Inventory(inventoryParameter.eventId(), inventoryParameter.capacity(), inventoryParameter.capacity(), 0L, 0L, 0L);
    }
}

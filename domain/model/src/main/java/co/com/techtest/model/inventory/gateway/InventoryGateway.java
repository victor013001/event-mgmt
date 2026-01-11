package co.com.techtest.model.inventory.gateway;

import co.com.techtest.model.inventory.Inventory;
import reactor.core.publisher.Mono;

public interface InventoryGateway {
    Mono<Inventory> saveInventory(Inventory inventory);

    Mono<Inventory> getEventInventory(String eventId);
}

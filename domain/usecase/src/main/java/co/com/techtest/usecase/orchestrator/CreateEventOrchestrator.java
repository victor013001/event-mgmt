package co.com.techtest.usecase.orchestrator;

import co.com.techtest.model.event.Event;
import co.com.techtest.model.event.EventParameter;
import co.com.techtest.model.inventory.InventoryParameter;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.model.util.exception.TechnicalException;
import co.com.techtest.usecase.event.EventUseCase;
import co.com.techtest.usecase.inventory.InventoryUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateEventOrchestrator {

    private final EventUseCase eventUseCase;
    private final InventoryUseCase inventoryUseCase;

    public Mono<Event> createEvent(EventParameter eventParameter) {
        return eventUseCase.createEvent(eventParameter)
                .flatMap(event -> inventoryUseCase.creteEventInventory(buildInventoryParameter(event))
                        .map(inventory -> event)
                        .onErrorResume(error -> eventUseCase.deleteEvent(event)
                                .then(error instanceof TechnicalException
                                        ? Mono.error(error)
                                        : Mono.error(new BusinessException(TechnicalMessageType.ERROR_MS_INTERNAL_SERVER)))));
    }

    private InventoryParameter buildInventoryParameter(Event event) {
        return new InventoryParameter(event.id(), event.capacity());
    }
}

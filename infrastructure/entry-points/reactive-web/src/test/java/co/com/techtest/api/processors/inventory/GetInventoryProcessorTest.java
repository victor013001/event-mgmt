package co.com.techtest.api.processors.inventory;

import co.com.techtest.api.dto.request.inventory.GetEventAvailabilityReqParams;
import co.com.techtest.model.inventory.Inventory;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.inventory.InventoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetInventoryProcessorTest {

    @Mock
    private InventoryUseCase inventoryUseCase;

    private GetInventoryProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new GetInventoryProcessor(inventoryUseCase);
    }

    @Test
    void shouldExecuteSuccessfully() {
        GetEventAvailabilityReqParams params = new GetEventAvailabilityReqParams("flow-123", "user-123", "event-123");
        Inventory inventory = new Inventory("event-123", 100L, 80L, 20L, 0L, 0L);

        when(inventoryUseCase.getEventInventory("event-123"))
                .thenReturn(Mono.just(inventory));

        Mono<ServerResponse> result = processor.execute(params, OperationType.GET_EVENT_AVAILABILITY);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(inventoryUseCase).getEventInventory("event-123");
    }

    @Test
    void shouldReturnBadRequestForInvalidEventId() {
        GetEventAvailabilityReqParams params = new GetEventAvailabilityReqParams("flow-123", "user-123", "");

        Mono<ServerResponse> result = processor.execute(params, OperationType.GET_EVENT_AVAILABILITY);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldReturnBadRequestForNullEventId() {
        GetEventAvailabilityReqParams params = new GetEventAvailabilityReqParams("flow-123", "user-123", null);

        Mono<ServerResponse> result = processor.execute(params, OperationType.GET_EVENT_AVAILABILITY);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldHandleBusinessException() {
        GetEventAvailabilityReqParams params = new GetEventAvailabilityReqParams("flow-123", "user-123", "event-123");
        BusinessException businessException = new BusinessException(TechnicalMessageType.ERROR_MS_INTERNAL_SERVER);

        when(inventoryUseCase.getEventInventory(anyString()))
                .thenReturn(Mono.error(businessException));

        Mono<ServerResponse> result = processor.execute(params, OperationType.GET_EVENT_AVAILABILITY);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }
}

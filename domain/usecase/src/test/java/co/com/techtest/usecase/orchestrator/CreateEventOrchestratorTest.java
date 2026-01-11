package co.com.techtest.usecase.orchestrator;

import co.com.techtest.model.event.Event;
import co.com.techtest.model.event.EventParameter;
import co.com.techtest.model.inventory.Inventory;
import co.com.techtest.model.inventory.InventoryParameter;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.model.util.exception.TechnicalException;
import co.com.techtest.usecase.event.EventUseCase;
import co.com.techtest.usecase.inventory.InventoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateEventOrchestratorTest {

    @Mock
    private EventUseCase eventUseCase;

    @Mock
    private InventoryUseCase inventoryUseCase;

    private CreateEventOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new CreateEventOrchestrator(eventUseCase, inventoryUseCase);
    }

    @Test
    void shouldCreateEventSuccessfully() {
        EventParameter parameter = new EventParameter("Concert", LocalDateTime.now().plusDays(1), "Medellin", 100L, "user-123");
        Event event = new Event("event-123", "Concert", LocalDateTime.now().plusDays(1), "Medellin", 100L, "user-123", System.currentTimeMillis());
        Inventory inventory = new Inventory("event-123", 100L, 100L, 0L, 0L, 0L);

        given(eventUseCase.createEvent(parameter)).willReturn(Mono.just(event));
        given(inventoryUseCase.creteEventInventory(any(InventoryParameter.class))).willReturn(Mono.just(inventory));

        StepVerifier.create(orchestrator.createEvent(parameter))
                .expectNext(event)
                .verifyComplete();

        verify(eventUseCase).createEvent(parameter);
        verify(inventoryUseCase).creteEventInventory(any(InventoryParameter.class));
        verify(eventUseCase, never()).deleteEvent(any(Event.class));
    }

    @Test
    void shouldDeleteEventWhenInventoryCreationFailsWithBusinessException() {
        EventParameter parameter = new EventParameter("Concert", LocalDateTime.now().plusDays(1), "Medellin", 100L, "user-123");
        Event event = new Event("event-123", "Concert", LocalDateTime.now().plusDays(1), "Medellin", 100L, "user-123", System.currentTimeMillis());
        BusinessException businessError = new BusinessException(TechnicalMessageType.ERROR_MS_INTERNAL_SERVER);

        given(eventUseCase.createEvent(parameter)).willReturn(Mono.just(event));
        given(inventoryUseCase.creteEventInventory(any(InventoryParameter.class))).willReturn(Mono.error(businessError));
        given(eventUseCase.deleteEvent(event)).willReturn(Mono.just(event));

        StepVerifier.create(orchestrator.createEvent(parameter))
                .expectErrorMatches(BusinessException.class::isInstance)
                .verify();

        verify(eventUseCase).createEvent(parameter);
        verify(inventoryUseCase).creteEventInventory(any(InventoryParameter.class));
        verify(eventUseCase).deleteEvent(event);
    }

    @Test
    void shouldDeleteEventAndConvertToTechnicalExceptionWhenInventoryCreationFailsWithOtherException() {
        EventParameter parameter = new EventParameter("Concert", LocalDateTime.now().plusDays(1), "Medellin", 100L, "user-123");
        Event event = new Event("event-123", "Concert", LocalDateTime.now().plusDays(1), "Medellin", 100L, "user-123", System.currentTimeMillis());
        RuntimeException runtimeError = new RuntimeException("Database error");

        given(eventUseCase.createEvent(parameter)).willReturn(Mono.just(event));
        given(inventoryUseCase.creteEventInventory(any(InventoryParameter.class))).willReturn(Mono.error(runtimeError));
        given(eventUseCase.deleteEvent(event)).willReturn(Mono.just(event));

        StepVerifier.create(orchestrator.createEvent(parameter))
                .expectErrorMatches(BusinessException.class::isInstance)
                .verify();

        verify(eventUseCase).createEvent(parameter);
        verify(inventoryUseCase).creteEventInventory(any(InventoryParameter.class));
        verify(eventUseCase).deleteEvent(event);
    }

    @Test
    void shouldDeleteEventAndPropagateTechnicalExceptionWhenInventoryCreationFailsWithTechnicalException() {
        EventParameter parameter = new EventParameter("Concert", LocalDateTime.now().plusDays(1), "Medellin", 100L, "user-123");
        Event event = new Event("event-123", "Concert", LocalDateTime.now().plusDays(1), "Medellin", 100L, "user-123", System.currentTimeMillis());
        TechnicalException technicalError = new TechnicalException(TechnicalMessageType.ERROR_MS_INTERNAL_SERVER);

        given(eventUseCase.createEvent(parameter)).willReturn(Mono.just(event));
        given(inventoryUseCase.creteEventInventory(any(InventoryParameter.class))).willReturn(Mono.error(technicalError));
        given(eventUseCase.deleteEvent(event)).willReturn(Mono.just(event));

        StepVerifier.create(orchestrator.createEvent(parameter))
                .expectErrorMatches(TechnicalException.class::isInstance)
                .verify();

        verify(eventUseCase).createEvent(parameter);
        verify(inventoryUseCase).creteEventInventory(any(InventoryParameter.class));
        verify(eventUseCase).deleteEvent(event);
    }

    @Test
    void shouldPropagateEventCreationError() {
        EventParameter parameter = new EventParameter("Concert", LocalDateTime.now().plusDays(1), "Medellin", 100L, "user-123");
        RuntimeException eventError = new RuntimeException("Event creation failed");

        given(eventUseCase.createEvent(parameter)).willReturn(Mono.error(eventError));

        StepVerifier.create(orchestrator.createEvent(parameter))
                .expectError(RuntimeException.class)
                .verify();

        verify(eventUseCase).createEvent(parameter);
        verify(inventoryUseCase, never()).creteEventInventory(any(InventoryParameter.class));
        verify(eventUseCase, never()).deleteEvent(any(Event.class));
    }
}

package co.com.techtest.usecase.event;

import co.com.techtest.model.event.Event;
import co.com.techtest.model.event.EventParameter;
import co.com.techtest.model.event.gateway.EventGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventUseCaseTest {

    @InjectMocks
    EventUseCase eventUseCase;

    @Mock
    EventGateway eventGateway;

    private static EventParameter validParam() {
        return new EventParameter(
                "Concert",
                LocalDateTime.now().plusMinutes(5),
                "Medellin",
                100L,
                "user-123");
    }

    private static EventParameter invalidParamPastOrNow() {
        return new EventParameter(
                "Concert",
                LocalDateTime.now(),
                "Medellin",
                100L,
                "user-123");
    }

    @Test
    void createEvent_success() {
        var param = validParam();

        when(eventGateway.saveEvent(any(Event.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(eventUseCase.createEvent(param))
                .consumeNextWith(saved -> {
                    assertNotNull(saved.id());
                    assertEquals(param.name(), saved.name());
                    assertEquals(param.date(), saved.date());
                    assertEquals(param.place(), saved.place());
                    assertEquals(param.capacity(), saved.capacity());
                    assertEquals(param.xUserId(), saved.createdBy());
                    assertNotNull(saved.createdAt());
                })
                .verifyComplete();

        verify(eventGateway).saveEvent(any(Event.class));
    }

    @Test
    void createEvent_invalidDate_notAfterNow_shouldReturnBusinessException() {
        var param = invalidParamPastOrNow();

        StepVerifier.create(eventUseCase.createEvent(param))
                .expectErrorSatisfies(ex -> {
                    assertInstanceOf(BusinessException.class, ex);
                    BusinessException be = (BusinessException) ex;
                    assertEquals(TechnicalMessageType.ERROR_MS_INVALID_EVENT, be.getTechnicalMessage());
                })
                .verify();

        verify(eventGateway, never()).saveEvent(any(Event.class));
    }

    @Test
    void deleteEvent_success() {
        var event = new Event("event-123", "Concert", LocalDateTime.now().plusMinutes(5), "Medellin", 100L, "user-123", System.currentTimeMillis());

        when(eventGateway.deleteEvent(event)).thenReturn(Mono.just(event));

        StepVerifier.create(eventUseCase.deleteEvent(event))
                .expectNext(event)
                .verifyComplete();

        verify(eventGateway).deleteEvent(event);
    }

    @Test
    void deleteEvent_gatewayReturnsError_shouldPropagateError() {
        var event = new Event("event-123", "Concert", LocalDateTime.now().plusMinutes(5), "Medellin", 100L, "user-123", System.currentTimeMillis());
        RuntimeException gatewayError = new RuntimeException("delete failed");

        when(eventGateway.deleteEvent(event)).thenReturn(Mono.error(gatewayError));

        StepVerifier.create(eventUseCase.deleteEvent(event))
                .expectErrorSatisfies(ex -> assertSame(gatewayError, ex))
                .verify();

        verify(eventGateway).deleteEvent(event);
    }
}

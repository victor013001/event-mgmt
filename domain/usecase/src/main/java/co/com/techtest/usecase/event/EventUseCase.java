package co.com.techtest.usecase.event;

import co.com.techtest.model.event.Event;
import co.com.techtest.model.event.EventParameter;
import co.com.techtest.model.event.gateway.EventGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class EventUseCase {

  private final EventGateway eventGateway;

  public Mono<Event> createEvent(EventParameter eventParameter) {
    return Mono.just(eventParameter)
        .filter(this::isValidEventParameter)
        .map(this::buildEvent)
        .flatMap(eventGateway::saveEvent)
        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessageType.ERROR_MS_INVALID_EVENT))));
  }

  private boolean isValidEventParameter(EventParameter eventParameter) {
    return eventParameter.date().isAfter(LocalDateTime.now());
  }

  private Event buildEvent(EventParameter eventParameter) {
    return new Event(UUID.randomUUID().toString(), eventParameter.name(), eventParameter.date(),
        eventParameter.place(), eventParameter.capacity(), eventParameter.userId(), System.currentTimeMillis());
  }
}

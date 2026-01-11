package co.com.techtest.model.event.gateway;

import co.com.techtest.model.event.Event;
import reactor.core.publisher.Mono;

public interface EventGateway {
  Mono<Event> saveEvent(Event event);
}

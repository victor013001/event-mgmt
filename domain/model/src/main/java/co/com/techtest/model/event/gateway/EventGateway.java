package co.com.techtest.model.event.gateway;

import co.com.techtest.model.event.Event;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventGateway {
    Mono<Event> saveEvent(Event event);

    Mono<List<Event>> findEventsByPlace(String place);

    Mono<Event> deleteEvent(Event event);
}

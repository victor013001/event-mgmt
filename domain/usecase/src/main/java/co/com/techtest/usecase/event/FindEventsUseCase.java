package co.com.techtest.usecase.event;

import co.com.techtest.model.event.Event;
import co.com.techtest.model.event.gateway.EventGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class FindEventsUseCase {

    private final EventGateway eventGateway;

    public Mono<List<Event>> findEventsByPlace(String place) {
        return eventGateway.findEventsByPlace(place);
    }
}

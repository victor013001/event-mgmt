package co.com.techtest.dynamodb.event;

import co.com.techtest.dynamodb.event.repository.EventRepository;
import co.com.techtest.model.event.Event;
import co.com.techtest.model.event.gateway.EventGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventDynamoAdapter implements EventGateway {

    private final EventRepository eventRepository;

    private static final String SAVE_EVENT_REQUEST = "Save Event Dynamo Adapter";
    private static final String SAVE_EVENT_KEY_REQUEST = "saveEventDynamoAdapterRQ";
    private static final String SAVE_EVENT_RESPONSE = "Save Event Dynamo Adapter Response";
    private static final String SAVE_EVENT_KEY_RESPONSE = "saveEventDynamoAdapterRS";
    private static final String SAVE_EVENT_ERROR_RESPONSE = "Save Event Dynamo Adapter Error Response";
    private static final String SAVE_EVENT_KEY_ERROR_RESPONSE = "saveEventDynamoAdapterErrorRS";

    private static final String GET_EVENTS_BY_PLACE_REQUEST = "Get Event By Place Dynamo Adapter";
    private static final String GET_EVENTS_BY_PLACE_KEY_REQUEST = "getEventsByPlaceDynamoAdapterRQ";
    private static final String GET_EVENTS_BY_PLACE_RESPONSE = "Get Event By Place Dynamo Adapter Response";
    private static final String GET_EVENTS_BY_PLACE_KEY_RESPONSE = "getEventsByPlaceDynamoAdapterRS";
    private static final String GET_EVENTS_BY_PLACE_ERROR_RESPONSE = "Get Event By Place Dynamo Adapter Error Response";
    private static final String GET_EVENTS_BY_PLACE_KEY_ERROR_RESPONSE = "getEventsByPlaceDynamoAdapterErrorRS";

    @Override
    public Mono<Event> saveEvent(Event event) {
        return eventRepository.save(event)
                .doOnSubscribe(_ -> log.info(SAVE_EVENT_REQUEST, kv(SAVE_EVENT_KEY_REQUEST, event)))
                .doOnNext(saved -> log.info(SAVE_EVENT_RESPONSE, kv(SAVE_EVENT_KEY_RESPONSE, saved)))
                .doOnError(error -> log.error(SAVE_EVENT_ERROR_RESPONSE, kv(SAVE_EVENT_KEY_ERROR_RESPONSE, error)))
                .onErrorMap(DynamoDbException.class, exception -> new TechnicalException(exception, TechnicalMessageType.ERROR_MS_DYNAMO_ERROR));
    }

    @Override
    public Mono<List<Event>> findEventsByPlace(String place) {
        return eventRepository.getAllEventsByPlace(place)
                .doOnSubscribe(_ -> log.info(GET_EVENTS_BY_PLACE_REQUEST, kv(GET_EVENTS_BY_PLACE_KEY_REQUEST, place)))
                .doOnNext(events -> log.info(GET_EVENTS_BY_PLACE_RESPONSE, kv(GET_EVENTS_BY_PLACE_KEY_RESPONSE, events)))
                .doOnError(error -> log.error(GET_EVENTS_BY_PLACE_ERROR_RESPONSE, kv(GET_EVENTS_BY_PLACE_KEY_ERROR_RESPONSE, error)))
                .onErrorMap(DynamoDbException.class, exception -> new TechnicalException(exception, TechnicalMessageType.ERROR_MS_DYNAMO_ERROR));
    }
}

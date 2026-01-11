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

  @Override
  public Mono<Event> saveEvent(Event event) {
    return eventRepository.save(event)
        .doOnSubscribe(_ -> log.info(SAVE_EVENT_REQUEST, kv(SAVE_EVENT_KEY_REQUEST, event)))
        .doOnNext(saved -> log.info(SAVE_EVENT_RESPONSE, kv(SAVE_EVENT_KEY_RESPONSE, saved)))
        .doOnError(error -> log.error(SAVE_EVENT_ERROR_RESPONSE, kv(SAVE_EVENT_KEY_ERROR_RESPONSE, error)))
        .onErrorMap(DynamoDbException.class, exception -> new TechnicalException(exception, TechnicalMessageType.ERROR_MS_DYNAMO_ERROR));
  }
}

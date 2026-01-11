package co.com.techtest.api.handler.event;

import co.com.techtest.api.dto.request.event.CreateEventRequest;
import co.com.techtest.api.dto.request.standardstructure.HeaderRequest;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.techtest.api.utils.HeadersUtilApi.buildHeaders;
import static co.com.techtest.api.utils.ResponseUtilApi.buildFallback;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateEventHandler {

  private final CreateEventProcessors createEventProcessors;

  private static final String CREATE_EVENT = "createEvent";
  private static final String FALLBACK_METHOD_NAME = "fallback";

  @CircuitBreaker(name = CREATE_EVENT, fallbackMethod = FALLBACK_METHOD_NAME)
  public Mono<ServerResponse> handle(ServerRequest request) {
    return request.bodyToMono(CreateEventRequest.class)
        .map(createEventRequest -> this.addHeaders(createEventRequest, buildHeaders(request)))
        .flatMap(createEventProcessors::process);
  }

  public Mono<ServerResponse> fallback(ServerRequest request, Exception exception) {
    return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.CREATE_EVENT, exception);
  }

  public Mono<ServerResponse> fallback(ServerRequest request, CallNotPermittedException callNotPermittedException) {
    return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.CREATE_EVENT, callNotPermittedException);
  }

  private CreateEventRequest addHeaders(CreateEventRequest createEventRequest, HeaderRequest headerRequest) {
    return createEventRequest.toBuilder()
        .xUserId(headerRequest.xUserId())
        .flowId(headerRequest.flowId())
        .build();
  }
}

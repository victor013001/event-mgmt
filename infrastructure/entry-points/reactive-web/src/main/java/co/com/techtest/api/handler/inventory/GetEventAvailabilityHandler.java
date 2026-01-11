package co.com.techtest.api.handler.inventory;

import co.com.techtest.api.dto.request.inventory.GetEventAvailabilityReqParams;
import co.com.techtest.api.dto.request.standardstructure.HeaderRequest;
import co.com.techtest.api.processors.inventory.GetInventoryProcessor;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.techtest.api.utils.HeadersUtilApi.buildHeaders;
import static co.com.techtest.api.utils.ResponseUtilApi.buildFallback;
import static co.com.techtest.api.utils.params.inventory.GetEventAvailabilityParam.EVENT_ID_PATH_VARIABLE;

@Component
@RequiredArgsConstructor
public class GetEventAvailabilityHandler {

    private final GetInventoryProcessor getInventoryProcessor;

    private static final String GET_EVENT_AVAILABILITY = "getEventAvailability";
    private static final String FALLBACK_METHOD_NAME = "fallback";

    @CircuitBreaker(name = GET_EVENT_AVAILABILITY, fallbackMethod = FALLBACK_METHOD_NAME)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return Mono.just(request)
                .flatMap(this::addHeaders)
                .flatMap(params -> getInventoryProcessor.execute(params, OperationType.GET_EVENT_AVAILABILITY));
    }

    public Mono<ServerResponse> fallback(ServerRequest request, Exception exception) {
        return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.GET_EVENT_AVAILABILITY, exception);
    }

    public Mono<ServerResponse> fallback(ServerRequest request, CallNotPermittedException callNotPermittedException) {
        return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.GET_EVENT_AVAILABILITY, callNotPermittedException);
    }

    private Mono<GetEventAvailabilityReqParams> addHeaders(ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(req -> req.pathVariable(EVENT_ID_PATH_VARIABLE))
                .map(eventId -> addHeaders(eventId, buildHeaders(serverRequest)))
                .onErrorResume(e -> Mono.just(addHeaders(null, buildHeaders(serverRequest))));
    }

    private GetEventAvailabilityReqParams addHeaders(String eventId, HeaderRequest headerRequest) {
        return new GetEventAvailabilityReqParams(headerRequest.flowId(), headerRequest.xUserId(), eventId);
    }
}

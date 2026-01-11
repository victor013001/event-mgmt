package co.com.techtest.api.handler.event;

import co.com.techtest.api.dto.request.event.GetEventRequestParams;
import co.com.techtest.api.dto.request.standardstructure.HeaderRequest;
import co.com.techtest.api.processors.GetEventsProcessor;
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
import static co.com.techtest.api.utils.params.event.GetEventsParam.PLACE_PARAM;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetEventsHandler {

    private final GetEventsProcessor getEventsProcessor;

    private static final String GET_EVENTS = "getEvents";
    private static final String FALLBACK_METHOD_NAME = "fallback";

    @CircuitBreaker(name = GET_EVENTS, fallbackMethod = FALLBACK_METHOD_NAME)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return Mono.just(request)
                .map(req -> addHeaders(req.queryParam(PLACE_PARAM).orElse(null), buildHeaders(req)))
                .flatMap(params -> getEventsProcessor.execute(params, OperationType.GET_EVENTS));
    }

    public Mono<ServerResponse> fallback(ServerRequest request, Exception exception) {
        return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.GET_EVENTS, exception);
    }

    public Mono<ServerResponse> fallback(ServerRequest request, CallNotPermittedException callNotPermittedException) {
        return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.GET_EVENTS, callNotPermittedException);
    }

    private GetEventRequestParams addHeaders(String place, HeaderRequest headerRequest) {
        return new GetEventRequestParams(headerRequest.flowId(), headerRequest.xUserId(), place);
    }
}

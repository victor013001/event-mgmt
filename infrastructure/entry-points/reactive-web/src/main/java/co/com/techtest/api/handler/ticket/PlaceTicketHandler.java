package co.com.techtest.api.handler.ticket;

import co.com.techtest.api.dto.request.standardstructure.HeaderRequest;
import co.com.techtest.api.dto.request.ticket.PlaceTicketReqParams;
import co.com.techtest.api.dto.request.ticket.PlaceTicketRequest;
import co.com.techtest.api.processors.ticket.PlaceTicketProcessor;
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
public class PlaceTicketHandler {

    private final PlaceTicketProcessor placeTicketProcessor;

    private static final String PLACE_TICKET = "placeTicket";
    private static final String FALLBACK_METHOD_NAME = "fallback";

    @CircuitBreaker(name = PLACE_TICKET, fallbackMethod = FALLBACK_METHOD_NAME)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.bodyToMono(PlaceTicketRequest.class)
                .flatMap(body -> buildParams(request, body))
                .flatMap(params -> placeTicketProcessor.execute(params, OperationType.PLACE_EVENT_TICKET));
    }

    public Mono<ServerResponse> fallback(ServerRequest request, Exception exception) {
        return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.PLACE_EVENT_TICKET, exception);
    }

    public Mono<ServerResponse> fallback(ServerRequest request, CallNotPermittedException callNotPermittedException) {
        return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.PLACE_EVENT_TICKET, callNotPermittedException);
    }

    private Mono<PlaceTicketReqParams> buildParams(ServerRequest serverRequest, PlaceTicketRequest body) {
        return Mono.just(serverRequest)
                .map(req -> req.pathVariable(EVENT_ID_PATH_VARIABLE))
                .map(eventId -> addPathAndHeaders(eventId, buildHeaders(serverRequest), body))
                .onErrorResume(e -> Mono.just(addPathAndHeaders(null, buildHeaders(serverRequest), body)));
    }

    private PlaceTicketReqParams addPathAndHeaders(String eventId, HeaderRequest headerRequest, PlaceTicketRequest body) {
        return new PlaceTicketReqParams(headerRequest.flowId(), headerRequest.xUserId(), eventId, body.quantity());
    }
}

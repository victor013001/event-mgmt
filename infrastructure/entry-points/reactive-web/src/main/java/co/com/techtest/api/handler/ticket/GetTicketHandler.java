package co.com.techtest.api.handler.ticket;

import co.com.techtest.api.dto.request.standardstructure.HeaderRequest;
import co.com.techtest.api.dto.request.ticket.GetTicketRequestParams;
import co.com.techtest.api.processors.ticket.GetTicketProcessor;
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
import static co.com.techtest.api.utils.params.ticket.GetTicketParam.TICKET_ID_PATH_VARIABLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetTicketHandler {

    private final GetTicketProcessor getTicketProcessor;

    private static final String GET_TICKET = "getTicket";
    private static final String FALLBACK_METHOD_NAME = "fallback";

    @CircuitBreaker(name = GET_TICKET, fallbackMethod = FALLBACK_METHOD_NAME)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return Mono.just(request)
                .map(req -> addHeaders(req.pathVariable(TICKET_ID_PATH_VARIABLE), buildHeaders(req)))
                .flatMap(params -> getTicketProcessor.execute(params, OperationType.GET_TICKET));
    }

    public Mono<ServerResponse> fallback(ServerRequest request, Exception exception) {
        return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.GET_TICKET, exception);
    }

    public Mono<ServerResponse> fallback(ServerRequest request, CallNotPermittedException callNotPermittedException) {
        return buildFallback(request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.GET_TICKET, callNotPermittedException);
    }

    private GetTicketRequestParams addHeaders(String ticketId, HeaderRequest headerRequest) {
        return new GetTicketRequestParams(headerRequest.flowId(), headerRequest.xUserId(), ticketId);
    }
}

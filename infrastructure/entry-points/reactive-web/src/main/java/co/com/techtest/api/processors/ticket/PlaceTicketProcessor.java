package co.com.techtest.api.processors.ticket;

import co.com.techtest.api.dto.request.ticket.PlaceTicketReqParams;
import co.com.techtest.api.mapper.TicketApiMapper;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.orchestrator.TicketOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

import static co.com.techtest.api.utils.ResponseUtilApi.buildResponseBadRequest;
import static co.com.techtest.api.utils.ResponseUtilApi.buildResponseBusinessError;
import static co.com.techtest.api.utils.ResponseUtilApi.buildResponseSuccess;
import static co.com.techtest.api.utils.ResponseUtilApi.logRequest;
import static co.com.techtest.api.utils.validator.ticket.PlaceTicketParamsValidator.validatePlaceTicketParams;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceTicketProcessor {

    private final TicketOrchestrator ticketOrchestrator;

    public Mono<ServerResponse> execute(PlaceTicketReqParams params, OperationType operationType) {
        return validatePlaceTicketParams(params)
                .filter(Predicate.not(List::isEmpty))
                .flatMap(errors -> buildResponseBadRequest(errors, params.flowId(), operationType))
                .switchIfEmpty(Mono.defer(() -> executeUseCase(params, operationType)))
                .doOnSubscribe(_ -> logRequest(operationType, params));
    }

    private Mono<ServerResponse> executeUseCase(PlaceTicketReqParams params, OperationType operationType) {
        return ticketOrchestrator.placeTicket(TicketApiMapper.MAPPER.toDomain(params))
                .map(TicketApiMapper.MAPPER::toResponse)
                .flatMap(ticketResponse -> buildResponseSuccess(ticketResponse, operationType, params.flowId()))
                .onErrorResume(BusinessException.class, error -> buildResponseBusinessError(error, operationType, params.flowId()));
    }
}

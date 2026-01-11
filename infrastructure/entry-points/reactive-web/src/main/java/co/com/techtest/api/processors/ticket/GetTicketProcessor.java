package co.com.techtest.api.processors.ticket;

import co.com.techtest.api.dto.request.ticket.GetTicketRequestParams;
import co.com.techtest.api.mapper.TicketApiMapper;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.ticket.GetTicketUseCase;
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
import static co.com.techtest.api.utils.validator.ticket.GetTicketParamsValidator.validateTicketIdParam;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetTicketProcessor {

    private final GetTicketUseCase getTicketUseCase;

    public Mono<ServerResponse> execute(GetTicketRequestParams params, OperationType operationType) {
        return validateTicketIdParam(params.ticketId())
                .filter(Predicate.not(List::isEmpty))
                .flatMap(errors -> buildResponseBadRequest(errors, params.flowId(), operationType))
                .switchIfEmpty(Mono.defer(() -> executeUseCase(params, operationType)))
                .doOnSubscribe(_ -> logRequest(operationType, params));
    }

    private Mono<ServerResponse> executeUseCase(GetTicketRequestParams params, OperationType operationType) {
        return getTicketUseCase.getTicketById(params.ticketId(), params.xUserId())
                .map(TicketApiMapper.MAPPER::toResponse)
                .flatMap(ticketResponse -> buildResponseSuccess(ticketResponse, operationType, params.flowId()))
                .onErrorResume(BusinessException.class, error -> buildResponseBusinessError(error, operationType, params.flowId()));
    }
}

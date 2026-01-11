package co.com.techtest.api.processors;

import co.com.techtest.api.dto.request.event.CreateEventRequest;
import co.com.techtest.api.mapper.EventApiMapper;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.orchestrator.CreateEventOrchestrator;
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
import static co.com.techtest.api.utils.validator.event.CreateEventValidator.validateCreateEventRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateEventProcessor {

    private final CreateEventOrchestrator createEventOrchestrator;

    public Mono<ServerResponse> execute(CreateEventRequest createEventRequest, OperationType operationType) {
        return validateCreateEventRequest(createEventRequest)
                .filter(Predicate.not(List::isEmpty))
                .flatMap(errors -> buildResponseBadRequest(errors, createEventRequest.flowId(), operationType))
                .switchIfEmpty(Mono.defer(() -> executeUseCase(createEventRequest, operationType)))
                .doOnSubscribe(_ -> logRequest(operationType, createEventRequest));
    }

    private Mono<ServerResponse> executeUseCase(CreateEventRequest createEventRequest, OperationType operationType) {
        return createEventOrchestrator.createEvent(EventApiMapper.MAPPER.toParameter(createEventRequest))
                .map(EventApiMapper.MAPPER::toResponse)
                .flatMap(eventResponse -> buildResponseSuccess(eventResponse, operationType, createEventRequest.flowId()))
                .onErrorResume(BusinessException.class, error -> buildResponseBusinessError(error, operationType, createEventRequest.flowId()));
    }

}

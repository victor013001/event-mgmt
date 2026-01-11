package co.com.techtest.api.processors.inventory;

import co.com.techtest.api.dto.request.inventory.GetEventAvailabilityReqParams;
import co.com.techtest.api.mapper.InventoryApiMapper;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.inventory.InventoryUseCase;
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
import static co.com.techtest.api.utils.validator.inventory.GetEventAvailabilityParamsValidator.validateAvailabilityParam;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetInventoryProcessor {

    private final InventoryUseCase inventoryUseCase;

    public Mono<ServerResponse> execute(GetEventAvailabilityReqParams params, OperationType operationType) {
        return validateAvailabilityParam(params.eventId())
                .filter(Predicate.not(List::isEmpty))
                .flatMap(errors -> buildResponseBadRequest(errors, params.flowId(), operationType))
                .switchIfEmpty(Mono.defer(() -> executeUseCase(params, operationType)))
                .doOnSubscribe(_ -> logRequest(operationType, params));
    }

    private Mono<ServerResponse> executeUseCase(GetEventAvailabilityReqParams params, OperationType operationType) {
        return inventoryUseCase.getEventInventory(params.eventId())
                .map(InventoryApiMapper.MAPPER::toResponse)
                .flatMap(eventsResponse -> buildResponseSuccess(eventsResponse, operationType, params.flowId()))
                .onErrorResume(BusinessException.class, error -> buildResponseBusinessError(error, operationType, params.flowId()));
    }
}

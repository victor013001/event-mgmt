package co.com.techtest.api.processors.event;

import co.com.techtest.api.dto.request.event.GetEventRequestParams;
import co.com.techtest.api.mapper.EventApiMapper;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.usecase.event.FindEventsUseCase;
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
import static co.com.techtest.api.utils.validator.event.GetEventsParamsValidator.validatePlaceParam;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetEventsProcessor {

    private final FindEventsUseCase findEventsUseCase;

    public Mono<ServerResponse> execute(GetEventRequestParams params, OperationType operationType) {
        return validatePlaceParam(params.place())
                .filter(Predicate.not(List::isEmpty))
                .flatMap(errors -> buildResponseBadRequest(errors, params.flowId(), operationType))
                .switchIfEmpty(Mono.defer(() -> executeUseCase(params, operationType)))
                .doOnSubscribe(_ -> logRequest(operationType, params));
    }

    private Mono<ServerResponse> executeUseCase(GetEventRequestParams params, OperationType operationType) {
        return findEventsUseCase.findEventsByPlace(params.place())
                .map(EventApiMapper.MAPPER::toResponseList)
                .flatMap(eventsResponse -> buildResponseSuccess(eventsResponse, operationType, params.flowId()))
                .onErrorResume(BusinessException.class, error -> buildResponseBusinessError(error, operationType, params.flowId()));
    }
}

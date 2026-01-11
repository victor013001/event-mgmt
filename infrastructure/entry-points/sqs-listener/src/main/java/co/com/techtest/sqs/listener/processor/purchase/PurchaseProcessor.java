package co.com.techtest.sqs.listener.processor.purchase;

import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.model.util.exception.TechnicalException;
import co.com.techtest.sqs.listener.dto.purchase.PurchaseReqParams;
import co.com.techtest.sqs.listener.utils.validator.PurchaseParamsValidator;
import co.com.techtest.usecase.ticket.UpdateTicketUseCase;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.function.Predicate;

import static co.com.techtest.parser.ParserUtilityApi.jsonStringToObject;
import static co.com.techtest.sqs.listener.utils.LoggerUtil.buildBadRequestLog;
import static co.com.techtest.sqs.listener.utils.LoggerUtil.buildResponseBusinessErrorLog;
import static co.com.techtest.sqs.listener.utils.LoggerUtil.logRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class PurchaseProcessor {

    private final UpdateTicketUseCase updateTicketUseCase;

    public Mono<Void> execute(Message message, OperationType operationType) {
        return buildMessage(message)
                .flatMap(req -> PurchaseParamsValidator.validatePlaceTicketParams(req)
                        .filter(Predicate.not(List::isEmpty))
                        .flatMap(errors -> buildBadRequestLog(errors, req.flowId(), operationType))
                        .switchIfEmpty(Mono.defer(() -> executeUseCase(req, operationType)))
                        .doOnSubscribe(_ -> logRequest(operationType, req)));
    }

    private Mono<Void> executeUseCase(PurchaseReqParams req, OperationType operationType) {
        return updateTicketUseCase.updateTicketStatus(req.ticketId(), TicketStatus.SOLD)
                .then()
                .onErrorResume(BusinessException.class, error -> buildResponseBusinessErrorLog(error, operationType, req.flowId()).then());
    }

    private Mono<PurchaseReqParams> buildMessage(Message message) {
        return jsonStringToObject(message.body(), new TypeReference<PurchaseReqParams>() {
        })
                .onErrorMap(Exception.class, ex -> new TechnicalException(ex, TechnicalMessageType.JSON_PROCESSING_ERROR));
    }
}

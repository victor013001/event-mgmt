package co.com.techtest.sqs.listener.utils;

import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.sqs.listener.dto.log.ErrorLogDetail;
import co.com.techtest.sqs.listener.dto.log.StandardLogData;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@UtilityClass
public class LoggerUtil {

    public static final String KV_RESPONSE = "RS";
    public static final String NAME_RESPONSE = "Response";

    private static final String ERROR_DETAILS_RESPONSE_MESSAGE = "Error Details Response";
    private static final String ERROR_DETAILS_RESPONSE_KEY = "ErrorDetailsRS";
    private static final String ERROR_BUSINESS_RESPONSE_MESSAGE = "Error Business Response";
    private static final String ERROR_BUSINESS_RESPONSE_KEY = "ErrorBusinessRS";

    public static Mono<Void> buildBadRequestLog(List<ErrorLogDetail> errors, String flowId, OperationType operation) {
        StandardLogData<Object> standardResponse = new StandardLogData<>(TechnicalMessageType.ERROR_MS_BAD_REQUEST.getCode(),
                TechnicalMessageType.ERROR_MS_BAD_REQUEST.getExternalMessage(), flowId, LocalDateTime.now().toString(), null, errors);
        logResponse(standardResponse, operation, null);
        return Mono.empty();
    }

    public static Mono<Void> buildResponseBusinessErrorLog(BusinessException error, OperationType operation, String flowId) {
        StandardLogData<Object> standardResponse = new StandardLogData<>(error.getTechnicalMessage().getCode(),
                error.getTechnicalMessage().getExternalMessage(), flowId, LocalDateTime.now().toString(), null, null);
        logResponse(standardResponse, operation, error);
        return Mono.empty();
    }

    public static void logRequest(OperationType operation, Object request) {
        log.info(operation.getNameRequest(), kv(operation.getKvRequest(), request));
    }

    private static void logResponse(StandardLogData<?> standardResponse, OperationType operationType, Throwable throwable) {
        if (hasErrors(standardResponse)) {
            logErrorDetails(standardResponse, operationType);
        } else if (isBusinessException(throwable)) {
            logBusinessError(standardResponse, operationType);
        } else {
            logSuccessResponse(standardResponse, operationType);
        }
    }

    private static boolean hasErrors(StandardLogData<?> standardResponse) {
        return Objects.nonNull(standardResponse.errors()) && !standardResponse.errors().isEmpty();
    }

    private static void logErrorDetails(StandardLogData<?> standardResponse, OperationType operation) {
        log.info(operation.getNameResponse().replace(NAME_RESPONSE, ERROR_DETAILS_RESPONSE_MESSAGE),
                kv(operation.getKvResponse().replace(KV_RESPONSE, ERROR_DETAILS_RESPONSE_KEY), standardResponse.errors()));
    }

    private static boolean isBusinessException(Throwable throwable) {
        return throwable instanceof BusinessException;
    }

    private static void logBusinessError(StandardLogData<?> standardResponse, OperationType operation) {
        log.info(operation.getNameResponse().replace(NAME_RESPONSE, ERROR_BUSINESS_RESPONSE_MESSAGE),
                kv(operation.getKvResponse().replace(KV_RESPONSE, ERROR_BUSINESS_RESPONSE_KEY), standardResponse));
    }

    private static void logSuccessResponse(StandardLogData<?> standardResponse, OperationType operation) {
        log.info(operation.getNameResponse(), kv(operation.getKvResponse(), standardResponse));
    }
}

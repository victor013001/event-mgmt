package co.com.techtest.api.utils;

import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import co.com.techtest.api.dto.response.standardstructure.StandardResponse;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static co.com.techtest.api.utils.HeadersUtilApi.FLOW_ID;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@UtilityClass
public class ResponseUtilApi {

    public static final String KV_RESPONSE = "RS";
    public static final String NAME_RESPONSE = "Response";

    private static final String ERROR_DETAILS_RESPONSE_MESSAGE = "Error Details Response";
    private static final String ERROR_DETAILS_RESPONSE_KEY = "ErrorDetailsRS";
    private static final String ERROR_BUSINESS_RESPONSE_MESSAGE = "Error Business Response";
    private static final String ERROR_BUSINESS_RESPONSE_KEY = "ErrorBusinessRS";
    private static final String ERROR_FALLBACK_OPEN_RESPONSE_MESSAGE = "Error Fallback Open Response";
    private static final String ERROR_FALLBACK_OPEN_RESPONSE_KEY = "ErrorFallbackOpenRS";
    private static final String ERROR_FALLBACK_RESPONSE_MESSAGE = "Error Fallback Response";
    private static final String ERROR_FALLBACK_RESPONSE_KEY = "ErrorFallbackRS";

    public static Mono<ServerResponse> buildFallback(ServerRequest request, TechnicalMessageType technicalMessageType,
                                                     OperationType operationType, Throwable throwable) {
        StandardResponse<Object> standardResponse = new StandardResponse<>(technicalMessageType.getCode(), technicalMessageType.getExternalMessage(),
                request.headers().firstHeader(FLOW_ID), LocalDateTime.now().toString(), null, null);
        return buildResponse(standardResponse, operationType, technicalMessageType, throwable);
    }

    public static Mono<ServerResponse> buildResponseBadRequest(List<ErrorDetail> errors, String flowId, OperationType operation) {
        StandardResponse<Object> standardResponse = new StandardResponse<>(TechnicalMessageType.ERROR_MS_BAD_REQUEST.getCode(),
                TechnicalMessageType.ERROR_MS_BAD_REQUEST.getExternalMessage(), flowId, LocalDateTime.now().toString(), null, errors);
        return buildResponse(standardResponse, operation, TechnicalMessageType.ERROR_MS_BAD_REQUEST, null);
    }

    public static <T> Mono<ServerResponse> buildResponseSuccess(T response, OperationType operation, String flowId) {
        StandardResponse<T> standardResponse = new StandardResponse<>(TechnicalMessageType.SUCCESS.getCode(),
                TechnicalMessageType.SUCCESS.getExternalMessage(), flowId, LocalDateTime.now().toString(), response, null);
        return buildResponse(standardResponse, operation, TechnicalMessageType.SUCCESS, null);
    }

    public static Mono<ServerResponse> buildResponseBusinessError(BusinessException error, OperationType operation, String flowId) {
        StandardResponse<Object> standardResponse = new StandardResponse<>(error.getTechnicalMessage().getCode(),
                error.getTechnicalMessage().getExternalMessage(), flowId, LocalDateTime.now().toString(), null, null);
        return buildResponse(standardResponse, operation, error.getTechnicalMessage(), error);
    }

    public static void logRequest(OperationType operation, Object request) {
        log.info(operation.getNameRequest(), kv(operation.getKvRequest(), request));
    }

    private static Mono<ServerResponse> buildResponse(StandardResponse<?> standardResponse, OperationType operationType,
                                                      TechnicalMessageType technicalMessageType, Throwable throwable) {
        return Mono.defer(() -> {
            HttpStatus httpStatus = resolveStatus(technicalMessageType);
            logResponse(standardResponse, operationType, throwable);
            return ServerResponse
                    .status(httpStatus)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(standardResponse);
        });
    }

    private static HttpStatus resolveStatus(TechnicalMessageType technicalMessageType) {
        return Optional.ofNullable(technicalMessageType.getExternalCode())
                .map(HttpStatus::resolve)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static void logResponse(StandardResponse<?> standardResponse, OperationType operationType, Throwable throwable) {
        if (hasErrors(standardResponse)) {
            logErrorDetails(standardResponse, operationType);
        } else if (isBusinessException(throwable)) {
            logBusinessError(standardResponse, operationType);
        } else if (hasThrowable(throwable)) {
            logFallbackError(standardResponse, operationType, throwable);
        } else {
            logSuccessResponse(standardResponse, operationType);
        }
    }

    private static boolean hasErrors(StandardResponse<?> standardResponse) {
        return Objects.nonNull(standardResponse.errors()) && !standardResponse.errors().isEmpty();
    }

    private static void logErrorDetails(StandardResponse<?> standardResponse, OperationType operation) {
        log.info(operation.getNameResponse().replace(NAME_RESPONSE, ERROR_DETAILS_RESPONSE_MESSAGE),
                kv(operation.getKvResponse().replace(KV_RESPONSE, ERROR_DETAILS_RESPONSE_KEY), standardResponse.errors()));
    }

    private static boolean isBusinessException(Throwable throwable) {
        return throwable instanceof BusinessException;
    }

    private static void logBusinessError(StandardResponse<?> standardResponse, OperationType operation) {
        log.info(operation.getNameResponse().replace(NAME_RESPONSE, ERROR_BUSINESS_RESPONSE_MESSAGE),
                kv(operation.getKvResponse().replace(KV_RESPONSE, ERROR_BUSINESS_RESPONSE_KEY), standardResponse));
    }

    private static boolean hasThrowable(Throwable throwable) {
        return Objects.nonNull(throwable);
    }

    private static void logFallbackError(StandardResponse<?> standardResponse, OperationType operation, Throwable throwable) {
        boolean isCircuitBreakerException = throwable instanceof CallNotPermittedException;
        String nameError = isCircuitBreakerException ? ERROR_FALLBACK_OPEN_RESPONSE_MESSAGE : ERROR_FALLBACK_RESPONSE_MESSAGE;
        String kvName = isCircuitBreakerException ? ERROR_FALLBACK_OPEN_RESPONSE_KEY : ERROR_FALLBACK_RESPONSE_KEY;

        log.info(operation.getNameResponse().replace(NAME_RESPONSE, nameError),
                kv(operation.getKvResponse().replace(KV_RESPONSE, kvName), standardResponse));
    }

    private static void logSuccessResponse(StandardResponse<?> standardResponse, OperationType operation) {
        log.info(operation.getNameResponse(), kv(operation.getKvResponse(), standardResponse));
    }
}

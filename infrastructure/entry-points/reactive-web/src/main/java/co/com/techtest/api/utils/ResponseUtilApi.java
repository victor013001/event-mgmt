package co.com.techtest.api.utils;

import co.com.techtest.api.dto.response.standardstructure.StandardResponse;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static co.com.techtest.api.utils.HeadersUtilApi.FLOW_ID;
import static co.com.techtest.model.util.enums.TechnicalMessageType.ERROR_MS_INVALID_HEADERS;

@Slf4j
@UtilityClass
public class ResponseUtilApi {

  public static Mono<ServerResponse> buildFallback(ServerRequest request, TechnicalMessageType technicalMessageType,
                                                   OperationType operationType, Throwable throwable) {
    StandardResponse<Object> standardResponse = new StandardResponse<>(technicalMessageType.getCode(), technicalMessageType.getExternalMessage(),
        request.headers().firstHeader(FLOW_ID), LocalDateTime.now().toString(), null, null);
    return buildResponse(standardResponse, operationType, technicalMessageType, throwable);
  }

  private static Mono<ServerResponse> buildResponse(StandardResponse<Object> standardResponse, OperationType operationType,
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
    return Optional.of(technicalMessageType.getExternalCode())
        .map(HttpStatus::resolve)
        .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private static void logResponse(StandardResponse<Object> standardResponse, OperationType operationType, Throwable throwable) {
    if(hasErrors(standardResponse)) {
      logErrorsDetails(standardResponse, operationType);
    } else if (isBusinessException(throwable)) {
      logBusinessError(standardResponse, operationType);
    } else if (hasThorwable(throwable)) {
      logFallbackError(standardResponse, operationType);
    } else {
      logSuccessResponse(standardResponse, operationType);
    }
  }

  private static boolean hasErrors(StandardResponse<Object> standardResponse) {
    return Objects.nonNull(standardResponse.errors()) && !standardResponse.errors().isEmpty();
  }

  private static void logErrorsDetails(StandardResponse<Object> standardResponse, OperationType operationType) {
    log.error(operationType.getNameResponse().replace());
  }
}

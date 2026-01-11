package co.com.techtest.api.utils;

import co.com.techtest.api.dto.request.standardstructure.HeaderRequest;
import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import co.com.techtest.api.dto.response.standardstructure.StandardResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static co.com.techtest.api.utils.validator.standardstructure.HeaderValidateUtil.validateHeaders;
import static co.com.techtest.model.util.enums.TechnicalMessageType.ERROR_MS_INVALID_HEADERS;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@UtilityClass
public class HeadersUtilApi {

  public static final String X_USER_ID = "X-User-Id";
  public static final String FLOW_ID = "flowId";

  public static final String VALIDATE_REQUIRED_HEADERS_ERROR = "Validate Require Headers Error";
  public static final String VALIDATE_REQUIRED_HEADERS_KEY_ERROR = "validateRequireHeadersError";

  public static Mono<ServerResponse> requireHeaders(ServerRequest request,
                                                    Function<ServerRequest, Mono<ServerResponse>> next) {
    HeaderRequest headerRequest = buildHeaders(request);
    return validateHeaders(headerRequest)
        .filter(Predicate.not(List::isEmpty))
        .flatMap(errors -> {
          log.error(VALIDATE_REQUIRED_HEADERS_ERROR, kv(VALIDATE_REQUIRED_HEADERS_KEY_ERROR, errors));
          return ServerResponse.status(HttpStatus.BAD_REQUEST)
              .bodyValue(buildStandardResponse(errors));
        })
        .switchIfEmpty(Mono.defer(() -> next.apply(request)));
  }

  public static HeaderRequest buildHeaders(ServerRequest request) {
    ServerRequest.Headers headers = request.headers();
    return new HeaderRequest(headers.firstHeader(X_USER_ID), headers.firstHeader(FLOW_ID));
  }

  private static StandardResponse<Object> buildStandardResponse(List<ErrorDetail> errors) {
    return new StandardResponse<>(ERROR_MS_INVALID_HEADERS.getCode(), ERROR_MS_INVALID_HEADERS.getExternalMessage(),
        null, LocalDateTime.now().toString(), null, errors);
  }
}

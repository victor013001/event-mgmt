package co.com.techtest.api.utils;

import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResponseUtilApiTest {

    private static final String FLOW_ID = "flow-123";

    @Test
    void shouldBuildResponseBadRequestWithSingleError() {
        ErrorDetail error = new ErrorDetail("400", "Invalid request");
        List<ErrorDetail> errors = List.of(error);

        Mono<ServerResponse> response = ResponseUtilApi.buildResponseBadRequest(
                errors, FLOW_ID, OperationType.CREATE_EVENT);

        StepVerifier.create(response)
                .assertNext(serverResponse -> {
                    assertEquals(HttpStatus.BAD_REQUEST, serverResponse.statusCode());
                    assertNotNull(serverResponse);
                })
                .verifyComplete();
    }

    @Test
    void shouldBuildResponseBadRequestWithEmptyErrorsList() {
        List<ErrorDetail> errors = Collections.emptyList();

        Mono<ServerResponse> response = ResponseUtilApi.buildResponseBadRequest(
                errors, FLOW_ID, OperationType.CREATE_EVENT);

        StepVerifier.create(response)
                .assertNext(serverResponse -> {
                    assertEquals(HttpStatus.BAD_REQUEST, serverResponse.statusCode());
                    assertNotNull(serverResponse);
                })
                .verifyComplete();
    }

    @Test
    void shouldBuildResponseBusinessErrorWithBadRequestType() {
        BusinessException businessException = new BusinessException(TechnicalMessageType.ERROR_MS_BAD_REQUEST);

        Mono<ServerResponse> response = ResponseUtilApi.buildResponseBusinessError(
                businessException, OperationType.CREATE_EVENT, FLOW_ID);

        StepVerifier.create(response)
                .assertNext(serverResponse -> {
                    assertEquals(HttpStatus.BAD_REQUEST, serverResponse.statusCode());
                    assertNotNull(serverResponse);
                })
                .verifyComplete();
    }

    @Test
    void shouldBuildResponseBusinessErrorWithInternalServerError() {
        BusinessException businessException = new BusinessException(TechnicalMessageType.ERROR_MS_INTERNAL_SERVER);

        Mono<ServerResponse> response = ResponseUtilApi.buildResponseBusinessError(
                businessException, OperationType.CREATE_EVENT, FLOW_ID);

        StepVerifier.create(response)
                .assertNext(serverResponse -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, serverResponse.statusCode());
                    assertNotNull(serverResponse);
                })
                .verifyComplete();
    }

    @Test
    void shouldBuildFallbackWithGenericError() {
        MockServerRequest request = MockServerRequest.builder().header("flowId", FLOW_ID).build();
        RuntimeException throwable = new RuntimeException("Generic error");

        Mono<ServerResponse> response = ResponseUtilApi.buildFallback(
                request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.CREATE_EVENT, throwable);

        StepVerifier.create(response)
                .assertNext(serverResponse -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, serverResponse.statusCode());
                    assertNotNull(serverResponse);
                })
                .verifyComplete();
    }

    @Test
    void shouldBuildFallbackWithCallNotPermittedException() {
        MockServerRequest request = MockServerRequest.builder().header("flowId", FLOW_ID).build();
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("test-circuit-breaker");
        CallNotPermittedException throwable = CallNotPermittedException.createCallNotPermittedException(circuitBreaker);

        Mono<ServerResponse> response = ResponseUtilApi.buildFallback(
                request, TechnicalMessageType.ERROR_MS_INTERNAL_SERVER, OperationType.CREATE_EVENT, throwable);

        StepVerifier.create(response)
                .assertNext(serverResponse -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, serverResponse.statusCode());
                    assertNotNull(serverResponse);
                })
                .verifyComplete();
    }

    @Test
    void shouldBuildResponseSuccessWithData() {
        String responseData = "Event created successfully";

        Mono<ServerResponse> response = ResponseUtilApi.buildResponseSuccess(
                responseData, OperationType.CREATE_EVENT, FLOW_ID);

        StepVerifier.create(response)
                .assertNext(serverResponse -> {
                    assertEquals(HttpStatus.OK, serverResponse.statusCode());
                    assertNotNull(serverResponse);
                })
                .verifyComplete();
    }

    @Test
    void shouldBuildResponseSuccessWithNullData() {
        Mono<ServerResponse> response = ResponseUtilApi.buildResponseSuccess(
                null, OperationType.CREATE_EVENT, FLOW_ID);

        StepVerifier.create(response)
                .assertNext(serverResponse -> {
                    assertEquals(HttpStatus.OK, serverResponse.statusCode());
                    assertNotNull(serverResponse);
                })
                .verifyComplete();
    }

    @Test
    void shouldLogRequestWithValidData() {
        String requestData = "{\"name\":\"Test Event\"}";

        assertDoesNotThrow(() ->
                ResponseUtilApi.logRequest(OperationType.CREATE_EVENT, requestData)
        );
    }

    @Test
    void shouldLogRequestWithNullData() {
        assertDoesNotThrow(() ->
                ResponseUtilApi.logRequest(OperationType.CREATE_EVENT, null)
        );
    }
}

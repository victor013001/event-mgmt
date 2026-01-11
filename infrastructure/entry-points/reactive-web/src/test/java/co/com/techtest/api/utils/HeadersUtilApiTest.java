package co.com.techtest.api.utils;

import co.com.techtest.api.dto.request.standardstructure.HeaderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class HeadersUtilApiTest {

    @Test
    void shouldBuildHeadersWithValidHeaders() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Id", "user123")
                .header("flowId", "flow123")
                .build();

        HeaderRequest result = HeadersUtilApi.buildHeaders(request);

        assertEquals("user123", result.xUserId());
        assertEquals("flow123", result.flowId());
    }

    @Test
    void shouldBuildHeadersWithMissingHeaders() {
        MockServerRequest request = MockServerRequest.builder().build();

        HeaderRequest result = HeadersUtilApi.buildHeaders(request);

        assertNull(result.xUserId());
        assertNull(result.flowId());
    }

    @Test
    void shouldBuildHeadersWithPartialHeaders() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Id", "user123")
                .build();

        HeaderRequest result = HeadersUtilApi.buildHeaders(request);

        assertEquals("user123", result.xUserId());
        assertNull(result.flowId());
    }

    @Test
    void shouldRequireHeadersAndProceedWhenValid() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Id", "user123")
                .header("flowId", "flow123")
                .build();

        Function<ServerRequest, Mono<ServerResponse>> next = req ->
                ServerResponse.ok().bodyValue("Success");

        Mono<ServerResponse> result = HeadersUtilApi.requireHeaders(request, next);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }

    @Test
    void shouldRequireHeadersAndReturnBadRequestWhenInvalid() {
        MockServerRequest request = MockServerRequest.builder().build();

        Function<ServerRequest, Mono<ServerResponse>> next = req ->
                ServerResponse.ok().bodyValue("Success");

        Mono<ServerResponse> result = HeadersUtilApi.requireHeaders(request, next);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }

    @Test
    void shouldRequireHeadersAndReturnBadRequestWhenMissingUserId() {
        MockServerRequest request = MockServerRequest.builder()
                .header("flowId", "flow123")
                .build();

        Function<ServerRequest, Mono<ServerResponse>> next = req ->
                ServerResponse.ok().bodyValue("Success");

        Mono<ServerResponse> result = HeadersUtilApi.requireHeaders(request, next);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }

    @Test
    void shouldRequireHeadersAndReturnBadRequestWhenMissingFlowId() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Id", "user123")
                .build();

        Function<ServerRequest, Mono<ServerResponse>> next = req ->
                ServerResponse.ok().bodyValue("Success");

        Mono<ServerResponse> result = HeadersUtilApi.requireHeaders(request, next);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }
}

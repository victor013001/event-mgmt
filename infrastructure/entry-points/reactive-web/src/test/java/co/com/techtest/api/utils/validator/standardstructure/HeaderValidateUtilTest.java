package co.com.techtest.api.utils.validator.standardstructure;

import co.com.techtest.api.dto.request.standardstructure.HeaderRequest;
import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderValidateUtilTest {

    @Test
    void shouldReturnEmptyErrorsWhenHeadersAreValid() {
        HeaderRequest validHeaders = new HeaderRequest("user123", "flow123");

        Mono<List<ErrorDetail>> result = HeaderValidateUtil.validateHeaders(validHeaders);

        StepVerifier.create(result)
                .assertNext(errors -> assertEquals(0, errors.size()))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorsWhenUserIdIsNull() {
        HeaderRequest invalidHeaders = new HeaderRequest(null, "flow123");

        Mono<List<ErrorDetail>> result = HeaderValidateUtil.validateHeaders(invalidHeaders);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL006", errors.get(0).code());
                    assertEquals("The x user id is required.", errors.get(0).message());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorsWhenFlowIdIsNull() {
        HeaderRequest invalidHeaders = new HeaderRequest("user123", null);

        Mono<List<ErrorDetail>> result = HeaderValidateUtil.validateHeaders(invalidHeaders);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL005", errors.get(0).code());
                    assertEquals("The flow id is required.", errors.get(0).message());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorsWhenBothHeadersAreNull() {
        HeaderRequest invalidHeaders = new HeaderRequest(null, null);

        Mono<List<ErrorDetail>> result = HeaderValidateUtil.validateHeaders(invalidHeaders);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(2, errors.size());
                    assertEquals("VAL006", errors.get(0).code());
                    assertEquals("VAL005", errors.get(1).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorsWhenHeadersAreEmpty() {
        HeaderRequest invalidHeaders = new HeaderRequest("", "");

        Mono<List<ErrorDetail>> result = HeaderValidateUtil.validateHeaders(invalidHeaders);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(2, errors.size());
                    assertEquals("VAL006", errors.get(0).code());
                    assertEquals("VAL005", errors.get(1).code());
                })
                .verifyComplete();
    }
}

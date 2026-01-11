package co.com.techtest.sqs.listener.utils.validator;

import co.com.techtest.sqs.listener.dto.log.ErrorLogDetail;
import co.com.techtest.sqs.listener.dto.purchase.PurchaseReqParams;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PurchaseParamsValidatorTest {

    @Test
    void shouldReturnEmptyErrorsWhenParamsAreValid() {
        PurchaseReqParams validParams = new PurchaseReqParams(
                "user-123",
                "flow-123",
                "ticket-123"
        );

        Mono<List<ErrorLogDetail>> result = PurchaseParamsValidator.validatePlaceTicketParams(validParams);

        StepVerifier.create(result)
                .assertNext(errors -> assertEquals(0, errors.size()))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenUserIdIsNull() {
        PurchaseReqParams invalidParams = new PurchaseReqParams(
                null,
                "flow-123",
                "ticket-123"
        );

        Mono<List<ErrorLogDetail>> result = PurchaseParamsValidator.validatePlaceTicketParams(invalidParams);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL011", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenFlowIdIsEmpty() {
        PurchaseReqParams invalidParams = new PurchaseReqParams(
                "user-123",
                "",
                "ticket-123"
        );

        Mono<List<ErrorLogDetail>> result = PurchaseParamsValidator.validatePlaceTicketParams(invalidParams);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL008", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenTicketIdIsBlank() {
        PurchaseReqParams invalidParams = new PurchaseReqParams(
                "user-123",
                "flow-123",
                "   "
        );

        Mono<List<ErrorLogDetail>> result = PurchaseParamsValidator.validatePlaceTicketParams(invalidParams);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL015", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnMultipleErrorsWhenMultipleFieldsAreInvalid() {
        PurchaseReqParams invalidParams = new PurchaseReqParams(
                null,
                "",
                null
        );

        Mono<List<ErrorLogDetail>> result = PurchaseParamsValidator.validatePlaceTicketParams(invalidParams);

        StepVerifier.create(result)
                .assertNext(errors -> assertEquals(3, errors.size()))
                .verifyComplete();
    }
}

package co.com.techtest.api.utils.validator.ticket;

import co.com.techtest.api.dto.request.ticket.PlaceTicketReqParams;
import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaceTicketParamsValidatorTest {

    @Test
    void shouldReturnEmptyErrorsWhenRequestIsValid() {
        PlaceTicketReqParams validParams = PlaceTicketReqParams.builder()
                .eventId("event123")
                .quantity(2)
                .build();

        Mono<List<ErrorDetail>> result = PlaceTicketParamsValidator.validatePlaceTicketParams(validParams);

        StepVerifier.create(result)
                .assertNext(errors -> assertEquals(0, errors.size()))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenEventIdIsNull() {
        PlaceTicketReqParams invalidParams = PlaceTicketReqParams.builder()
                .eventId(null)
                .quantity(2)
                .build();

        Mono<List<ErrorDetail>> result = PlaceTicketParamsValidator.validatePlaceTicketParams(invalidParams);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL008", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenEventIdIsEmpty() {
        PlaceTicketReqParams invalidParams = PlaceTicketReqParams.builder()
                .eventId("")
                .quantity(2)
                .build();

        Mono<List<ErrorDetail>> result = PlaceTicketParamsValidator.validatePlaceTicketParams(invalidParams);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL008", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenQuantityIsZero() {
        PlaceTicketReqParams invalidParams = PlaceTicketReqParams.builder()
                .eventId("event123")
                .quantity(0)
                .build();

        Mono<List<ErrorDetail>> result = PlaceTicketParamsValidator.validatePlaceTicketParams(invalidParams);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL011", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenQuantityIsNegative() {
        PlaceTicketReqParams invalidParams = PlaceTicketReqParams.builder()
                .eventId("event123")
                .quantity(-1)
                .build();

        Mono<List<ErrorDetail>> result = PlaceTicketParamsValidator.validatePlaceTicketParams(invalidParams);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL011", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnMultipleErrorsWhenMultipleFieldsAreInvalid() {
        PlaceTicketReqParams invalidParams = PlaceTicketReqParams.builder()
                .eventId(null)
                .quantity(0)
                .build();

        Mono<List<ErrorDetail>> result = PlaceTicketParamsValidator.validatePlaceTicketParams(invalidParams);

        StepVerifier.create(result)
                .assertNext(errors -> assertEquals(2, errors.size()))
                .verifyComplete();
    }
}

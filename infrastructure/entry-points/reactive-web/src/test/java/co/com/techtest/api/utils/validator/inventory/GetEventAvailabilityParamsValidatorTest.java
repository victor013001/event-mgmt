package co.com.techtest.api.utils.validator.inventory;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class GetEventAvailabilityParamsValidatorTest {

    @Test
    void shouldReturnEmptyErrorsForValidEventId() {
        StepVerifier.create(GetEventAvailabilityParamsValidator.validateAvailabilityParam("event-123"))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorsForNullEventId() {
        StepVerifier.create(GetEventAvailabilityParamsValidator.validateAvailabilityParam(null))
                .expectNextMatches(errors -> !errors.isEmpty())
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorsForEmptyEventId() {
        StepVerifier.create(GetEventAvailabilityParamsValidator.validateAvailabilityParam(""))
                .expectNextMatches(errors -> !errors.isEmpty())
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorsForBlankEventId() {
        StepVerifier.create(GetEventAvailabilityParamsValidator.validateAvailabilityParam("   "))
                .expectNextMatches(errors -> !errors.isEmpty())
                .verifyComplete();
    }

    @Test
    void shouldValidateEventIdWithSpecialCharacters() {
        StepVerifier.create(GetEventAvailabilityParamsValidator.validateAvailabilityParam("event-123_test"))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }
}

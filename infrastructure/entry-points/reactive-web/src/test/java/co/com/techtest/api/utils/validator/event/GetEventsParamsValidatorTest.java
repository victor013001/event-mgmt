package co.com.techtest.api.utils.validator.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.test.StepVerifier;

import java.util.List;

class GetEventsParamsValidatorTest {

    @Test
    void shouldReturnEmptyErrorsForValidPlace() {
        StepVerifier.create(GetEventsParamsValidator.validatePlaceParam("Valid Place"))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldReturnErrorsForInvalidPlace(String place) {
        StepVerifier.create(GetEventsParamsValidator.validatePlaceParam(place))
                .expectNextMatches(errors -> !errors.isEmpty())
                .verifyComplete();
    }
}

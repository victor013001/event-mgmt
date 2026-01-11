package co.com.techtest.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.test.StepVerifier;

class ValidatorHelperTest {

    @Test
    void shouldReturnTrueForValidString() {
        StepVerifier.create(ValidatorHelper.isValidNotNullAndNotBlank("valid"))
                .expectNext(true)
                .verifyComplete();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldReturnFalseForInvalidString(String input) {
        StepVerifier.create(ValidatorHelper.isValidNotNullAndNotBlank(input))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueForPositiveNumber() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero(5L))
                .expectNext(true)
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    void shouldReturnFalseForNonPositiveNumber(Long input) {
        StepVerifier.create(ValidatorHelper.isHigherThanZero(input))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseForNullNumber() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero(null))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueForNonNullObject() {
        StepVerifier.create(ValidatorHelper.isNotNull("object"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseForNullObject() {
        StepVerifier.create(ValidatorHelper.isNotNull(null))
                .expectNext(false)
                .verifyComplete();
    }
}

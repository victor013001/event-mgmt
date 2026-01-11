package co.com.techtest.validator;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class ValidatorHelperTest {

    @Test
    void shouldReturnTrueForValidNotNullAndNotBlankString() {
        StepVerifier.create(ValidatorHelper.isValidNotNullAndNotBlank("valid"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseForNullString() {
        StepVerifier.create(ValidatorHelper.isValidNotNullAndNotBlank(null))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseForBlankString() {
        StepVerifier.create(ValidatorHelper.isValidNotNullAndNotBlank(""))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueForValidLong() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero(5L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseForZeroLong() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero(0L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueForValidInteger() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero(5))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseForZeroInteger() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero(0))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueForNotNullObject() {
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

    @Test
    void shouldReturnFalseForNullLong() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero((Long) null))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseForNegativeLong() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero(-1L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseForNullInteger() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero((Integer) null))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseForNegativeInteger() {
        StepVerifier.create(ValidatorHelper.isHigherThanZero(-1))
                .expectNext(false)
                .verifyComplete();
    }
}
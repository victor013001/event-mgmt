package co.com.techtest.api.utils.validator.ticket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.test.StepVerifier;

import java.util.List;

class GetTicketParamsValidatorTest {

    @Test
    void shouldReturnEmptyErrorsForValidTicketId() {
        StepVerifier.create(GetTicketParamsValidator.validateTicketIdParam("ticket-123"))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldReturnErrorsForInvalidTicketId(String ticketId) {
        StepVerifier.create(GetTicketParamsValidator.validateTicketIdParam(ticketId))
                .expectNextMatches(errors -> !errors.isEmpty())
                .verifyComplete();
    }
}
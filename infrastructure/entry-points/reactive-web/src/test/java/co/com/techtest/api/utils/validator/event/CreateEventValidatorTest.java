package co.com.techtest.api.utils.validator.event;

import co.com.techtest.api.dto.request.event.CreateEventRequest;
import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateEventValidatorTest {

    @Test
    void shouldReturnEmptyErrorsWhenRequestIsValid() {
        CreateEventRequest validRequest = CreateEventRequest.builder()
                .name("Test Event")
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(100L)
                .build();

        Mono<List<ErrorDetail>> result = CreateEventValidator.validateCreateEventRequest(validRequest);

        StepVerifier.create(result)
                .assertNext(errors -> assertEquals(0, errors.size()))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenNameIsNull() {
        CreateEventRequest invalidRequest = CreateEventRequest.builder()
                .name(null)
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(100L)
                .build();

        Mono<List<ErrorDetail>> result = CreateEventValidator.validateCreateEventRequest(invalidRequest);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL001", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenNameIsEmpty() {
        CreateEventRequest invalidRequest = CreateEventRequest.builder()
                .name("")
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(100L)
                .build();

        Mono<List<ErrorDetail>> result = CreateEventValidator.validateCreateEventRequest(invalidRequest);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL001", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenPlaceIsNull() {
        CreateEventRequest invalidRequest = CreateEventRequest.builder()
                .name("Test Event")
                .place(null)
                .date(LocalDateTime.now())
                .capacity(100L)
                .build();

        Mono<List<ErrorDetail>> result = CreateEventValidator.validateCreateEventRequest(invalidRequest);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL003", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenDateIsNull() {
        CreateEventRequest invalidRequest = CreateEventRequest.builder()
                .name("Test Event")
                .place("Test Place")
                .date(null)
                .capacity(100L)
                .build();

        Mono<List<ErrorDetail>> result = CreateEventValidator.validateCreateEventRequest(invalidRequest);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL002", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenCapacityIsZero() {
        CreateEventRequest invalidRequest = CreateEventRequest.builder()
                .name("Test Event")
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(0L)
                .build();

        Mono<List<ErrorDetail>> result = CreateEventValidator.validateCreateEventRequest(invalidRequest);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL004", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenCapacityIsNegative() {
        CreateEventRequest invalidRequest = CreateEventRequest.builder()
                .name("Test Event")
                .place("Test Place")
                .date(LocalDateTime.now())
                .capacity(-1L)
                .build();

        Mono<List<ErrorDetail>> result = CreateEventValidator.validateCreateEventRequest(invalidRequest);

        StepVerifier.create(result)
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("VAL004", errors.get(0).code());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnMultipleErrorsWhenMultipleFieldsAreInvalid() {
        CreateEventRequest invalidRequest = CreateEventRequest.builder()
                .name(null)
                .place("")
                .date(null)
                .capacity(0L)
                .build();

        Mono<List<ErrorDetail>> result = CreateEventValidator.validateCreateEventRequest(invalidRequest);

        StepVerifier.create(result)
                .assertNext(errors -> assertEquals(4, errors.size()))
                .verifyComplete();
    }
}

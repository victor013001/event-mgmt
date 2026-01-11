package co.com.techtest.sqs.listener.utils.validator;

import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.sqs.listener.dto.log.ErrorLogDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldValidatorUtilTest {

    private List<ErrorLogDetail> errors;

    @BeforeEach
    void setUp() {
        errors = new ArrayList<>();
    }

    @Test
    void shouldAddErrorWhenValidationFails() {
        FieldValidatorUtil.validateField(false, errors, TechnicalMessageType.ERROR_MS_INVALID_NAME);

        assertEquals(1, errors.size());
        assertEquals("VAL001", errors.get(0).code());
        assertEquals("The name is required.", errors.get(0).message());
    }

    @Test
    void shouldNotAddErrorWhenValidationPasses() {
        FieldValidatorUtil.validateField(true, errors, TechnicalMessageType.ERROR_MS_INVALID_NAME);

        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldNotAddErrorWhenValidationIsNull() {
        FieldValidatorUtil.validateField(null, errors, TechnicalMessageType.ERROR_MS_INVALID_DATE);

        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldAddMultipleErrors() {
        FieldValidatorUtil.validateField(false, errors, TechnicalMessageType.ERROR_MS_INVALID_NAME);
        FieldValidatorUtil.validateField(false, errors, TechnicalMessageType.ERROR_MS_INVALID_DATE);

        assertEquals(2, errors.size());
        assertEquals("VAL001", errors.get(0).code());
        assertEquals("VAL002", errors.get(1).code());
    }
}

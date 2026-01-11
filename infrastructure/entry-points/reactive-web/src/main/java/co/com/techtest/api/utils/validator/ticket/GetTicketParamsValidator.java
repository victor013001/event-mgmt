package co.com.techtest.api.utils.validator.ticket;

import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static co.com.techtest.api.utils.validator.standardstructure.FieldValidatorUtil.validateField;
import static co.com.techtest.validator.ValidatorHelper.isValidNotNullAndNotBlank;

@UtilityClass
public class GetTicketParamsValidator {

    public static Mono<List<ErrorDetail>> validateTicketIdParam(String ticketId) {
        return isValidNotNullAndNotBlank(ticketId)
                .map(GetTicketParamsValidator::validations);
    }

    private static List<ErrorDetail> validations(Boolean isValid) {
        List<ErrorDetail> errors = new ArrayList<>();
        validateField(isValid, errors, TechnicalMessageType.ERROR_MS_INVALID_TICKET_ID);
        return errors;
    }
}
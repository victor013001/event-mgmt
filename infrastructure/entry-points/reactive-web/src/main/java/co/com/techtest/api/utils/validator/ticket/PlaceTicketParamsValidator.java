package co.com.techtest.api.utils.validator.ticket;

import co.com.techtest.api.dto.request.ticket.PlaceTicketReqParams;
import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;

import static co.com.techtest.api.utils.validator.standardstructure.FieldValidatorUtil.validateField;
import static co.com.techtest.validator.ValidatorHelper.isHigherThanZero;
import static co.com.techtest.validator.ValidatorHelper.isValidNotNullAndNotBlank;

@UtilityClass
public class PlaceTicketParamsValidator {

    public static Mono<List<ErrorDetail>> validatePlaceTicketParams(PlaceTicketReqParams params) {
        return Mono.zip(isValidNotNullAndNotBlank(params.eventId()),
                        isHigherThanZero(params.quantity()))
                .map(PlaceTicketParamsValidator::validations);
    }

    private static List<ErrorDetail> validations(Tuple2<Boolean, Boolean> validations) {
        List<ErrorDetail> errors = new ArrayList<>();
        validateField(validations.getT1(), errors, TechnicalMessageType.ERROR_MS_INVALID_EVENT_ID);
        validateField(validations.getT2(), errors, TechnicalMessageType.ERROR_MS_INVALID_QUANTITY);
        return errors;
    }
}

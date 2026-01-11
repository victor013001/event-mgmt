package co.com.techtest.api.utils.validator.event;

import co.com.techtest.api.dto.request.event.CreateEventRequest;
import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.util.ArrayList;
import java.util.List;

import static co.com.techtest.api.utils.validator.standardstructure.FieldValidatorUtil.validateField;
import static co.com.techtest.validator.ValidatorHelper.isHigherThanZero;
import static co.com.techtest.validator.ValidatorHelper.isNotNull;
import static co.com.techtest.validator.ValidatorHelper.isValidNotNullAndNotBlank;

@UtilityClass
public class CreateEventValidator {

    public static Mono<List<ErrorDetail>> validateCreateEventRequest(CreateEventRequest request) {
        return Mono.zip(isValidNotNullAndNotBlank(request.name()),
                        isValidNotNullAndNotBlank(request.place()),
                        isNotNull(request.date()),
                        isHigherThanZero(request.capacity()))
                .map(CreateEventValidator::validations);
    }

    private static List<ErrorDetail> validations(Tuple4<Boolean, Boolean, Boolean, Boolean> validations) {
        List<ErrorDetail> errors = new ArrayList<>();
        validateField(validations.getT1(), errors, TechnicalMessageType.ERROR_MS_INVALID_NAME);
        validateField(validations.getT2(), errors, TechnicalMessageType.ERROR_MS_INVALID_PLACE);
        validateField(validations.getT3(), errors, TechnicalMessageType.ERROR_MS_INVALID_DATE);
        validateField(validations.getT4(), errors, TechnicalMessageType.ERROR_MS_INVALID_CAPACITY);
        return errors;
    }
}

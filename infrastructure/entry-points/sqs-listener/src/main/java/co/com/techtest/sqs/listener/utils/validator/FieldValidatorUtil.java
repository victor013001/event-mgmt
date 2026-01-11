package co.com.techtest.sqs.listener.utils.validator;

import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.sqs.listener.dto.log.ErrorLogDetail;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class FieldValidatorUtil {

    public static void validateField(Boolean isValid, List<ErrorLogDetail> errors, TechnicalMessageType technicalMessage) {
        if (Boolean.FALSE.equals(isValid)) {
            errors.add(buildErrorDetail(technicalMessage));
        }
    }

    private static ErrorLogDetail buildErrorDetail(TechnicalMessageType technicalMessageType) {
        return new ErrorLogDetail(technicalMessageType.getCode(), technicalMessageType.getMessage());
    }
}
